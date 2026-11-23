const userId = '00000000-0000-0000-0000-000000000001';

const cartItemsContainer = document.getElementById("cart-items");
const subtotalElement = document.getElementById("subtotal");
const totalElement = document.getElementById("total");
const cartMessage = document.getElementById("cart-message");
const checkoutButton = document.getElementById("checkout-btn");

function formatPrice(price) {
    return new Intl.NumberFormat("en-CA", {
        style: "currency",
        currency: "CAD"
    }).format(price);
}

function showMessage(message, isError = false) {
    cartMessage.textContent = message;
    cartMessage.className = isError ? "cart-message error" : "cart-message";
}

async function loadCart() {
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
        totalElement.textContext = formatPrice(0);

        checkoutButton.disabled = true;
        return;
    }

    checkoutButton.disabled = false;

    cart.items.forEach(item -> {
        const itemElement = createCarItem(item);
        cartItemsContainer.appendChild(itemElement);
    });

    subtotalElement.textContent = formatPrice(cart.total);
    totalElement.textContent = formatPrice(cart.total);
}

function renderEmptyCart() {
    const emptyCart = document.createElement("div");
    emptyCart.className = "empty-cart";

    const heading = document.createElement("h2");
    heading.textContext = "Your cart is empty";

    const message = document.createElement("p");
    message.textContext = "Browse out catalog to find your next read.";

    emptyCart.append(heading, message);
    cartItemsContainer.appendChild(emptyCart);
}

function createCartItem(item) {
    const container = document.createElement("div");
    container.className = "cart-item";

    const info = document.createElement("div");
    info.className = "item=info";

    const title = document.createElement("h3");
    title.textContext = item.title;

    const author = document.createElement("p");
    author.textContent = `By ${item.author}`;

    const price = document.createElement("p");
    price.className = "item-price";
    price.textContext = formatPrice(item.unitPrice);

    info.append(title, author, price);

    const qualityControls = document.createElement("div");
    quantityControls.className = "quantity-controls";

    const decreaseButton = document.createElement("button");
    decreaseButton.type = "button";
    decreaseButton.textContext = "-";
    decreaseButton.setAttribute("aria-label", "Decrease quantity");
    decreaseButton.dataset.action = "decrease";
    decreaseButton.dataset.bookId = item.bookId;

    const quantityDisplay = document.createElement("span");
    quantityDisplay.textContext = item.quantity.

    const increaseButton = document.createElement("span");
    increaseButton.type = "button";
    increaseButton.textContext = "+";
    increaseButton.setAttribute("aria-label", "Increase Quantity");
    increaseButton.dataset.action = "increase";
    increaseButton.dataset.bookId = item.bookId;

    quantityControls.append(
        decreaseButton,
        quantityDisplay,
        increaseButton
    );

    const subtotal = document.createElement("div");
    subtotal.className = "item-subtotal";
    subtotal.textContext = formatPrice(item.subtotal);

    const removeButton = document.createElement("button");
    removeButton.type = "button";
    removeButton.className = "remove=btn";
    removeButton.textContext = "Remove";
    removeButton.dataset.action = "remove";
    removeButton.dataset.bookId = item.bookId;

    container.append(
        info,
        qualityControls,
        subtotal,
        removeButton
    );

    return container;
}

async function updateQuantity(bookId, newQuantity) {
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
            throw new Error("Unable to Update Cart.");
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

    if (!button) {
        return;
    }

    const bookId = button.dataset.bookId;
    const action = button.dataset.action;

    const itemElement = button.closest(".cart-item");
    const quantityDisplay = itemElement.querySelector(".quantity-controls span");

    const currentQuantity = Number(quantityDisplay.textContext);

    if (action === "increase") {
        updateQuantity(bookId, currentQuantity + 1);
    }

    if (action === "decrease") {
        updateQuantity(bookId, currentQuantity - 1);
    }

    if (action === "remove") {
        updateQuantity(bookId, 0);
    }
});

checkoutButton.addEventListener("click", () => {
    alert("Checkout functionality has not been implemented yet");
})

loadCart();