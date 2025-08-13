const API_BASE = "http://localhost:8080";

// Utility functions
function createOption(value, text) {
    const option = document.createElement("option");
    option.value = value;
    option.textContent = text;
    return option;
}

function showMessage(elem, message, isError = false) {
    elem.textContent = message;
    elem.style.color = isError ? "red" : "green";
    setTimeout(() => { elem.textContent = ""; }, 3600);
}

// Customers
async function fetchCustomers() {
    const res = await fetch(API_BASE + "/customers");
    return await res.json();
}

async function addCustomer(name) {
    const res = await fetch(API_BASE + "/customers", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ name })
    });
    return await res.json();
}

// Inventory
async function fetchInventory() {
    const res = await fetch(API_BASE + "/inventory");
    return await res.json();
}

async function addInventoryItem(name, quantity) {
    const res = await fetch(API_BASE + "/inventory", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ name, quantity: parseInt(quantity) })
    });
    return await res.json();
}

async function deleteInventoryItem(itemId) {
    // Assume the backend supports DELETE /inventory/{id}
    const res = await fetch(API_BASE + "/inventory/" + itemId, { method: "DELETE" });
    return res.ok;
}

// Orders
async function placeOrder(customerId, itemId, quantity) {
    const res = await fetch(API_BASE + "/orders", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ customerId: parseInt(customerId), itemId: parseInt(itemId), quantity: parseInt(quantity) })
    });
    return await res.json();
}

// DOM Elements
const customerList = document.getElementById("customer-list");
const addCustomerForm = document.getElementById("add-customer-form");
const customerNameInput = document.getElementById("customer-name");

const inventoryList = document.getElementById("inventory-list");
const addInventoryForm = document.getElementById("add-inventory-form");
const itemNameInput = document.getElementById("item-name");
const itemQtyInput = document.getElementById("item-qty");

const orderCustomerSelect = document.getElementById("order-customer");
const orderItemSelect = document.getElementById("order-item");
const orderQtyInput = document.getElementById("order-qty");
const placeOrderForm = document.getElementById("place-order-form");
const orderResult = document.getElementById("order-result");

async function renderCustomers() {
    const customers = await fetchCustomers();
    customerList.innerHTML = "";
    orderCustomerSelect.innerHTML = '<option value="" disabled selected>Select Customer</option>';
    customers.forEach(c => {
        const li = document.createElement("li");
        li.textContent = `${c.name} (ID: ${c.id})`;
        customerList.appendChild(li);
        orderCustomerSelect.appendChild(createOption(c.id, c.name));
    });
}

async function renderInventory() {
    const items = await fetchInventory();
    inventoryList.innerHTML = "";
    orderItemSelect.innerHTML = '<option value="" disabled selected>Select Item</option>';
    items.forEach(item => {
        const li = document.createElement("li");
        li.textContent = `${item.name} (Qty: ${item.quantity})`;

        // Delete button
        const delBtn = document.createElement("button");
        delBtn.className = "delete-btn";
        delBtn.textContent = "Delete";
        delBtn.onclick = async () => {
            if (confirm("Are you sure to delete this item?")) {
                await deleteInventoryItem(item.id);
                renderInventory();
            }
        };
        li.appendChild(delBtn);

        inventoryList.appendChild(li);
        orderItemSelect.appendChild(createOption(item.id, item.name));
    });
}

addCustomerForm.onsubmit = async (e) => {
    e.preventDefault();
    const name = customerNameInput.value.trim();
    if (!name) return;
    await addCustomer(name);
    customerNameInput.value = "";
    renderCustomers();
};

addInventoryForm.onsubmit = async (e) => {
    e.preventDefault();
    const name = itemNameInput.value.trim();
    const qty = itemQtyInput.value;
    if (!name || !qty || parseInt(qty) <= 0) return;
    await addInventoryItem(name, qty);
    itemNameInput.value = "";
    itemQtyInput.value = "";
    renderInventory();
};

placeOrderForm.onsubmit = async (e) => {
    e.preventDefault();
    const custId = orderCustomerSelect.value;
    const itemId = orderItemSelect.value;
    const qty = orderQtyInput.value;
    if (!custId || !itemId || !qty || parseInt(qty) <= 0) return;
    const result = await placeOrder(custId, itemId, qty);
    if (result.status === "PLACED") {
        showMessage(orderResult, "Order placed successfully!");
    } else {
        showMessage(orderResult, "Order rejected (insufficient stock)", true);
    }
    orderQtyInput.value = "";
    renderInventory();
};

// Initial load
renderCustomers();
renderInventory();
