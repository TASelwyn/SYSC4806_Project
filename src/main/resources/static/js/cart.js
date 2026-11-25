(function () {
    const cartLink = document.getElementById("cart-link");

    if (!cartLink) return;

    function updateCartLink() {
        const userId = window.Amazin?.getCurrentUserId();

        if (!userId) {
            cartLink.href = "#";
            return;
        }

        cartLink.href = "/cart/" + encodeURIComponent(userId);

        console.log("Cart link updated:", cartLink.href);
    }

    window.addEventListener("amazin:userchange", updateCartLink);

    updateCartLink();

    window.addEventListener("load", updateCartLink);

    window.addEventListener("pageshow", updateCartLink);
})();

const cartItemsContainer = document.getElementById("cart-items");
const subtotalElement = document.getElementById("subtotal");
const totalElement = document.getElementById("total");
const cartMessage = document.getElementById("cart-message");
const checkoutButton = document.getElementById("checkout-btn");

if (cartItemsContainer) {

    function getCurrentUserId() {
        return window.Amazin?.getCurrentUserId();
    }

    function formatPrice(price) {
        return new Intl.NumberFormat("en-CA", {
            style: "currency",
            currency: "CAD"
        }).format(price);
    }

    function showMessage(message, isError = false) {
        if (!cartMessage) return;

        cartMessage.textContent = message;
        cartMessage.className = isError
            ? "cart-message error"
            : "cart-message";
    }

    async function loadCart() {
        const userId = getCurrentUserId();

        console.log("Loading Cart for User: ", userId)

        if (!userId) {
            showMessage("Please select a user first.", true);
            return;
        }

        showMessage("Loading Cart...");

        try {
            const response = await fetch(`/api/cart/${userId}`);

            if (!response.ok) {
                throw new Error("Unable to retrieve cart");
            }

            const cart = await response.json();

            showMessage("");
            renderCart(cart);

        } catch (error) {
            showMessage(error.message, true);
            console.error(error);
        }
    }

    function renderCart(cart) {
        cartItemsContainer.replaceChildren();

        if (!cart.items || cart.items.length === 0) {
            renderEmptyCart();

            subtotalElement.textContent = formatPrice(0);
            totalElement.textContent = formatPrice(0);

            checkoutButton.disabled = true;
            return;
        }

        checkoutButton.disabled = false;

        cart.items.forEach(item => {
            const itemElement = createCartItem(item);
            cartItemsContainer.appendChild(itemElement);
        });

        subtotalElement.textContent = formatPrice(cart.total);
        totalElement.textContent = formatPrice(cart.total);
    }

    function renderEmptyCart() {
        const emptyCart = document.createElement("div");
        emptyCart.className = "empty-cart";

        const heading = document.createElement("h2");
        heading.textContent = "Your cart is empty";

        const message = document.createElement("p");
        message.textContent =
            "Browse our catalog to find your next read.";

        emptyCart.append(heading, message);
        cartItemsContainer.appendChild(emptyCart);
    }

    function createCartItem(item) {
        const container = document.createElement("div");
        container.className = "cart-item";

        const info = document.createElement("div");
        info.className = "item-info";

        const title = document.createElement("h3");
        title.textContent = item.title;

        const author = document.createElement("p");
        author.textContent = `By ${item.author}`;

        const price = document.createElement("p");
        price.className = "item-price";
        price.textContent = formatPrice(item.unitPrice);

        info.append(title, author, price);

        const quantityControls = document.createElement("div");
        quantityControls.className = "quantity-controls";

        const decreaseButton = document.createElement("button");
        decreaseButton.type = "button";
        decreaseButton.textContent = "-";
        decreaseButton.setAttribute("aria-label", "Decrease quantity");
        decreaseButton.dataset.action = "decrease";
        decreaseButton.dataset.bookId = item.bookId;

        const quantityDisplay = document.createElement("span");
        quantityDisplay.textContent = item.quantity;

        const increaseButton = document.createElement("button");
        increaseButton.type = "button";
        increaseButton.textContent = "+";
        increaseButton.setAttribute("aria-label", "Increase quantity");
        increaseButton.dataset.action = "increase";
        increaseButton.dataset.bookId = item.bookId;

        quantityControls.append(
            decreaseButton,
            quantityDisplay,
            increaseButton
        );

        const subtotal = document.createElement("div");
        subtotal.className = "item-subtotal";
        subtotal.textContent = formatPrice(item.subtotal);

        const removeButton = document.createElement("button");
        removeButton.type = "button";
        removeButton.className = "remove-btn";
        removeButton.textContent = "Remove";
        removeButton.dataset.action = "remove";
        removeButton.dataset.bookId = item.bookId;

        container.append(
            info,
            quantityControls,
            subtotal,
            removeButton
        );

        return container;
    }

    async function updateQuantity(bookId, newQuantity) {
        const userId = getCurrentUserId();

        if (!userId) {
            showMessage("Please select a user first.", true);
            return;
        }

        try {
            showMessage("Updating Cart...");

            const response = await fetch(`/api/cart/${userId}`, {
                method: "PATCH",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    bookId: bookId,
                    quantity: newQuantity
                })
            });

            if (!response.ok) {
                throw new Error("Unable to update cart.");
            }

            const updatedCart = await response.json();

            showMessage("");
            renderCart(updatedCart);

        } catch (error) {
            showMessage(error.message, true);
            console.error(error);
        }
    }

    cartItemsContainer.addEventListener("click", event => {
        const button = event.target.closest("button[data-action]");

        if (!button) return;

        const bookId = button.dataset.bookId;
        const action = button.dataset.action;

        const itemElement = button.closest(".cart-item");
        const quantityDisplay =
            itemElement.querySelector(".quantity-controls span");

        const currentQuantity = Number(quantityDisplay.textContent);

        if (action === "increase") {
            updateQuantity(bookId, currentQuantity + 1);
        }

        if (action === "decrease" && currentQuantity > 1) {
            updateQuantity(bookId, currentQuantity - 1);
        }

        if (action === "remove") {
            updateQuantity(bookId, 0);
        }
    });

    if (checkoutButton) {
        checkoutButton.addEventListener("click", () => {
            alert("Checkout functionality has not been implemented yet");
        });
    }

    window.addEventListener("amazin:userchange", loadCart);

    loadCart();
}
