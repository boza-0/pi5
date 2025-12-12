function isNonEmptyString(s, max = 255) {
  return typeof s === 'string' && s.trim().length > 0 && s.length <= max;
}

function isPrice(p) {
  return typeof p === 'number' && !isNaN(p) && p >= 0 && p <= 999999.99;
}

function isInteger(n, { min = Number.MIN_SAFE_INTEGER, max = Number.MAX_SAFE_INTEGER } = {}) {
  return Number.isInteger(n) && n >= min && n <= max;
}

function isStock(n) {
  // stock must be a non-negative integer
  return isInteger(n, { min: 0 });
}

function isProviderId(n) {
  // provider_id can be null or a positive integer
  return n === null || isInteger(n, { min: 1 });
}

module.exports = {
  isNonEmptyString,
  isPrice,
  isInteger,
  isStock,
  isProviderId
};
