require('dotenv').config();
const express = require('express');
const cors = require('cors');

const productsRouter = require('./routes/products.routes');
const clientsRouter = require('./routes/clients.routes');
const ordersRouter = require('./routes/orders.routes'); // <-- add this

const app = express();
app.use(cors());
app.use(express.json());

// Health check
app.get('/health', (req, res) => res.json({ status: 'ok' }));

// Routers
app.use('/products', productsRouter);
app.use('/clients', clientsRouter);
app.use('/orders', ordersRouter); // <-- mount orders routes

const port = process.env.PORT || 3000;
app.listen(port, () => {
  console.log(`API listening at http://localhost:${port}`);
});
