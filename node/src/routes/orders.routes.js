const express = require('express');
const router = express.Router();
const {
  listOrders,
  getOrder,
  createOrder,
  updateOrder,
  deleteOrder,
  listOrderProducts,
  addOrderProduct,
  deleteOrderProduct
} = require('../controllers/orders.controller');

// --- Orders ---
// GET all orders
router.get('/', listOrders);

// GET one order by ID
router.get('/:id', getOrder);

// CREATE new order
router.post('/', createOrder);

// UPDATE order by ID
router.put('/:id', updateOrder);

// DELETE order by ID
router.delete('/:id', deleteOrder);

// --- Order products ---
// GET all products for a given order
router.get('/:id/products', listOrderProducts);

// ADD new product to an order
router.post('/:id/products', addOrderProduct);

// DELETE product from an order
router.delete('/:id/products/:itemId', deleteOrderProduct);

module.exports = router;
