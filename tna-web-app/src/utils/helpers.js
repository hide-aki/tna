export const defaultParsePaste = str => str.split(/\r\n|\n|\r/).map(row => row.split("\t"));

export const getDistinctValues = values => {
  if (!values) return values;
  if (!Array.isArray(values)) return values;
  let set = new Set();
  values.forEach(value => set.add(value));
  return [...set];
};

export const toFixed = (value, precision = 2) => {
  if (typeof value !== "number") return value;
  return value.toFixed(precision);
};

export const signedValue = value => {
  if (typeof value !== "number") return value;
  const sign = value > 0 ? "+" : "";
  return `${sign}${value}`;
};

export const toFixedSigned = (value, precision = 2) => {
  if (typeof value !== "number") return value;
  const sign = value > 0 ? "+" : "";
  return `${sign}${value.toFixed(precision)}`;
};

export const findMinValue = data => {
  if (!data || !Array.isArray(data)) return;
  let minValue = null;
  for (let i = 0; i < data.length; i++) {
    if (minValue == null || parseFloat(data[i]) < minValue) {
      minValue = data[i];
    }
  }
  return minValue;
};

// data is Array of object: {key: , weight: }
export const getWeightedMean = data => {
  const weightedSum = data.reduce((acc, el) => acc + el.key * el.weight, 0);
  const totalWeight = data.reduce((acc, el) => acc + el.weight, 0);
  return totalWeight > 0 ? weightedSum / totalWeight : 0;
};

export const cbOrderConfigSort = (a, b) => {
  if (a.color === b.color) {
    return a.lot - b.lot;
  } else {
    return a.color < b.color ? -1 : 1;
  }
};
