(function () {
    const checkoutPage = document.getElementById("checkout-page");
    const confirmationPage = document.getElementById("confirmation-page");

    if (!checkoutPage && !confirmationPage) return;

    const page = checkoutPage || confirmationPage;
    const userId = page.dataset.userId;

    const byId = id => document.getElementById(id);

    // Currency Format
    function formatMoney(cents) {
        return new Intl.NumberFormat("en-CA", {
            style: "currency",
            currency: "CAD"
        }).format(cents / 100);
    }

    function toCents(value) {
        return Math.round(Number(value) * 100);
    }

    // Calculate the subtotal with taxes
    function calculateTotals(subtotal) {
        const subtotalCents = toCents(subtotal);
        const taxCents = Math.round(subtotalCents * 0.13);

        return {
            subtotalCents,
            taxCents,
            totalCents: subtotalCents + taxCents
        };
    }

    function formatDate(date) {
        return new Intl.DateTimeFormat("en-CA", {
            year: "numeric",
            month: "long",
            day: "numeric"
        }).format(new Date(date));
    }

    function randomInt(min, max) {
        return Math.floor(Math.random() * (max - min + 1)) + min;
    }

    // Keep simulated details same during checkout session
    const draftKey = `amazin.checkout.draft.${userId}`;

    function readStorage(key) {
        try {
            return JSON.parse(sessionStorage.getItem(key));
        } catch (error) {
            return null;
        }
    }

    function writeStorage(key, value) {
        try {
            sessionStorage.setItem(key, JSON.stringify(value));
        } catch (error) {
            console.warn("Session storage unavailable.", error);
        }
    }

    // Info generator
    function generateCheckoutDetails(cartSignature) {
        const streets = [
            "Tenth Street",
            "Random Avenue",
            "Test Lane",
            "Check Drive"
        ];

        const street = streets[randomInt(0, streets.length - 1)];

        const cardType = Math.random() < 0.5
            ? "Visa"
            : "Mastercard";

        const lastFour = String(randomInt(0, 9999))
            .padStart(4, "0");

        // Random delivery date: 1 to 3 days after today
        const deliveryDate = new Date();
        deliveryDate.setHours(12, 0, 0, 0);
        deliveryDate.setDate(
            deliveryDate.getDate() + randomInt(1, 3)
        );

        return {
            cartSignature,
            orderDate: new Date().toDateString(),

            address:
                `${randomInt(100, 999)} ${street}, ` +
                "Ottawa, ON",

            payment:
                `${cardType} ending in ${lastFour} `,

            deliveryDate: deliveryDate.toISOString()
        };
    }

    function getCartSignature(cart) {
        return JSON.stringify(
            cart.items
                .map(item => [item.bookId, item.quantity])
                .sort((a, b) => a[0].localeCompare(b[0]))
        );
    }

    // fetch data from backend
    async function getJson(url, options = {}) {
        const response = await fetch(url, options);

        if (!response.ok) {
            let message = `Request failed (${response.status})`;

            try {
                const error = await response.json();
                message = error.detail || error.message || message;
            } catch (error) {
                // Some error responses have no JSON body.
            }

            throw new Error(message);
        }

        return response.json();
    }

    // Display books and their subtotals
    function renderItems(container, items) {
        container.replaceChildren();

        items.forEach(item => {
            const row = document.createElement("div");
            row.className = "item-line";

            const title = document.createElement("span");
            title.textContent = `${item.title} × ${item.quantity}`;

            const price = document.createElement("span");
            price.textContent = formatMoney(toCents(item.subtotal));

            row.append(title, price);
            container.appendChild(row);
        });
    }


    // CHECKOUT PAGE

    async function loadCheckout() {
        const message = byId("checkout-message");
        const placeOrderButton = byId("place-order");

        try {
            const cart = await getJson(
                `/api/cart/${encodeURIComponent(userId)}`
            );

            if (!cart.items || cart.items.length === 0) {
                message.className = "message error";
                message.textContent =
                    "Your cart is empty. Add books before checking out.";
                return;
            }

            const signature = getCartSignature(cart);

            let details = readStorage(draftKey);

            // Generate new details only when necessary
            if (!details ||
                details.cartSignature !== signature ||
                details.orderDate !== new Date().toDateString()) {

                details = generateCheckoutDetails(signature);
                writeStorage(draftKey, details);
            }

            // Show delivery details
            byId("delivery-address").textContent = details.address;
            byId("payment-method").textContent = details.payment;
            byId("delivery-date").textContent =
                formatDate(details.deliveryDate);

            const itemCount = cart.items.reduce(
                (sum, item) => sum + item.quantity,
                0
            );

            const totals = calculateTotals(cart.total);

            // Show order totals
            byId("checkout-item-count").textContent = itemCount;

            byId("checkout-subtotal").textContent =
                formatMoney(totals.subtotalCents);

            byId("checkout-tax").textContent =
                formatMoney(totals.taxCents);

            byId("checkout-total").textContent =
                formatMoney(totals.totalCents);

            renderItems(byId("checkout-items"), cart.items);

            message.textContent = "";
            placeOrderButton.disabled = false;

            // Complete the simulated purchase
            placeOrderButton.addEventListener("click", async () => {
                placeOrderButton.disabled = true;
                message.textContent = "Placing your order...";

                try {
                    // Backend saves order and reduces stock
                    const order = await getJson(
                        `/api/checkout/${encodeURIComponent(userId)}`,
                        { method: "POST" }
                    );

                    // Preserve delivery details for confirmation
                    writeStorage(
                        `amazin.checkout.order.${order.id}`,
                        details
                    );

                    try {
                        sessionStorage.removeItem(draftKey);
                    } catch (error) {
                        // Storage may be unavailable.
                    }

                    window.location.assign(
                        `/checkout/${encodeURIComponent(userId)}` +
                        `/confirmation/${encodeURIComponent(order.id)}`
                    );

                } catch (error) {
                    message.className = "message error";
                    message.textContent = error.message;
                    placeOrderButton.disabled = false;
                }
            });

        } catch (error) {
            message.className = "message error";
            message.textContent =
                `Unable to load checkout: ${error.message}`;
        }
    }

    // ORDER CONFIRMATION PAGE

    async function loadConfirmation() {
        const message = byId("confirmation-message");
        const orderId = confirmationPage.dataset.orderId;

        try {
            // Completed order and user information
            const [order, users] = await Promise.all([
                getJson(
                    `/api/orders/${encodeURIComponent(userId)}/` +
                    encodeURIComponent(orderId)
                ),
                getJson("/api/users")
            ]);

            const customer = users.find(user => user.id === userId);

            const firstName = customer
                ? customer.firstName
                : "Customer";

            const details = readStorage(
                `amazin.checkout.order.${orderId}`
            );

            const totals = calculateTotals(order.total);

            const itemCount = order.items.reduce(
                (sum, item) => sum + item.quantity,
                0
            );

            // Thank-you message
            byId("thank-you").textContent =
                `Thank you for your order, ${firstName}!`;

            byId("order-number").textContent =
                `Order number: ${order.id}`;

            // Display the same delivery information
            byId("confirmed-delivery").textContent = details
                ? formatDate(details.deliveryDate)
                : "Not available outside this checkout session";

            byId("confirmed-address").textContent = details
                ? `Delivery: ${details.address}`
                : "";

            byId("confirmed-payment").textContent = details
                ? `Payment: ${details.payment}`
                : "";

            // Final order summary
            byId("confirmed-item-count").textContent = itemCount;

            byId("confirmed-subtotal").textContent =
                formatMoney(totals.subtotalCents);

            byId("confirmed-tax").textContent =
                formatMoney(totals.taxCents);

            byId("confirmed-total").textContent =
                formatMoney(totals.totalCents);

            renderItems(byId("confirmed-items"), order.items);

            message.textContent = "";
            byId("confirmation-details").hidden = false;

        } catch (error) {
            message.className = "message error";
            message.textContent =
                `Unable to load order: ${error.message}`;
        }
    }


    if (checkoutPage) {
        loadCheckout();
    } else {
        loadConfirmation();
    }
})();
