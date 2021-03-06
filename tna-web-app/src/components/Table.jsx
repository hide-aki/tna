import React, { Component } from "react";
import PropTypes from "prop-types";
import classnames from "classnames";

import MuiTable from "@material-ui/core/Table";
import TableBody from "@material-ui/core/TableBody";
import TableCell from "@material-ui/core/TableCell";
import TableHead from "@material-ui/core/TableHead";
import TableRow from "@material-ui/core/TableRow";
import MuiCheckbox from "@material-ui/core/Checkbox";
import MuiTextField from "@material-ui/core/TextField";
import MenuItem from "@material-ui/core/MenuItem";
import Select from "@material-ui/core/Select";

import withStyles from "@material-ui/core/styles/withStyles";

import DateFnsUtils from "@date-io/moment";
import { MuiPickersUtilsProvider, KeyboardDatePicker } from "@material-ui/pickers";

export const DateInput = ({ rowIdx, colIdx, column, record, onChange, width = 120, disabled = false, format = "ll" }) => (
  <MuiPickersUtilsProvider utils={DateFnsUtils}>
    <KeyboardDatePicker
      disableToolbar
      variant="inline"
      format={format}
      margin="normal"
      id={`date-picker-inline-${rowIdx}-${colIdx}`}
      value={record[column.dataKey]}
      onChange={onChange && onChange({ rowIdx, colIdx, column, record })}
      KeyboardButtonProps={{
        "aria-label": "change date"
      }}
      disabled={disabled}
    />
  </MuiPickersUtilsProvider>
);

export const CheckBox = ({ rowIdx, colIdx, column, record, onChange }) => (
  <MuiCheckbox
    style={{ paddingTop: 0, paddingBottom: 0 }}
    checked={record[column.dataKey] || false}
    onChange={onChange && onChange({ rowIdx, colIdx, column, record })}
  />
);

export const TextInput = ({ rowIdx, colIdx, column, record, onChange, type, width = 120 }) => (
  <MuiTextField
    style={{ width }}
    type={type}
    value={record[column.dataKey] === null ? "" : record[column.dataKey]}
    onChange={onChange && onChange({ rowIdx, colIdx, column, record })}
  />
);

export const SelectInput = ({ rowIdx, colIdx, column, record, onChange, choices, multiple, allowEmpty, width = 135 }) => {
  let options = typeof choices === "function" ? choices({ rowIdx, colIdx, column, record }) : choices;
  if (allowEmpty && !multiple) {
    options.unshift({});
  }
  let value = record[column.dataKey];
  if (!value) {
    value = !multiple ? (allowEmpty ? "" : options[0] && options[0].id) : [];
  }
  return (
    <Select style={{ width }} multiple={multiple} value={value} onChange={onChange && onChange({ rowIdx, colIdx, column, record })}>
      {options.map((option, idx) => (
        <MenuItem key={idx} value={option.id} style={{ padding: allowEmpty && idx === 0 ? "16px" : "8px" }}>
          {option.name}
        </MenuItem>
      ))}
    </Select>
  );
};

export const LinkField = ({ rowIdx, colIdx, column, record, color = "blue", onClick }) => (
  <div style={{ color, cursor: "pointer" }} onClick={onClick && onClick({ rowIdx, colIdx, column, record })}>
    {record[column.dataKey]}
  </div>
);

export const DefaultRowRenderer = ({ columns, record, rowIdx }) => {
  return (
    <TableRow key={rowIdx}>
      {columns.map(({ dataKey, align = "left", element, rowSpan, ...rest }, colIdx) => {
        let rSpan = 1;
        if (rowSpan && typeof rowSpan === "function") {
          rSpan = rowSpan({ rowIdx, colIdx, record, column: columns[colIdx] });
        }
        return rSpan > 0 ? (
          <TableCell key={colIdx} rowSpan={rSpan} align={align}>
            {element && React.isValidElement(element)
              ? React.cloneElement(element, {
                  rowIdx,
                  colIdx,
                  column: { ...rest, dataKey, align },
                  record
                })
              : record[dataKey] === null
              ? "-"
              : record[dataKey]}
          </TableCell>
        ) : null;
      })}
    </TableRow>
  );
};

const styles = {
  table: {},
  fixedHeader: {
    position: "sticky",
    top: 0,
    backgroundColor: "white"
  }
};

class Table extends Component {
  state = {
    gridViewScroll: undefined
  };

  render() {
    const { classes, id, columns, rows, rowRenderer, rowSelectAll, onSelectAll, fixedHeader } = this.props;
    return (
      <MuiTable className={classes.table} id={id}>
        <TableHead>
          <TableRow>
            {columns.map(({ dataKey, title, align = "left", selectAll }, idx) => (
              <TableCell key={idx} align={align} className={classnames({ [classes.fixedHeader]: fixedHeader })}>
                {selectAll && (
                  <MuiCheckbox
                    style={{ paddingTop: 0, paddingBottom: 0 }}
                    checked={rowSelectAll[dataKey] || false}
                    onChange={e => onSelectAll && onSelectAll(dataKey, e.target.checked)}
                    indeterminate={rowSelectAll[dataKey] === null}
                  />
                )}
                {title}
              </TableCell>
            ))}
          </TableRow>
        </TableHead>
        <TableBody>
          {rows.map((record, rowIdx) =>
            typeof rowRenderer === "function"
              ? rowRenderer({ columns, record, rowIdx })
              : React.cloneElement(rowRenderer, { columns, record, rowIdx, key: rowIdx })
          )}
        </TableBody>
      </MuiTable>
    );
  }
}

Table.propTypes = {
  fixedHeader: PropTypes.bool,
  columns: PropTypes.arrayOf(
    PropTypes.shape({
      dataKey: PropTypes.string,
      title: PropTypes.string,
      align: PropTypes.oneOf(["left", "right", "center"]),
      element: PropTypes.element,
      rowSpan: PropTypes.func, // ({rowIdx, colIdx, record, column}) => Number (rowSpan: 0 or more)
      selectAll: PropTypes.bool
    })
  ),
  rows: PropTypes.arrayOf(PropTypes.object),
  rowRenderer: PropTypes.oneOfType([PropTypes.element, PropTypes.func]),
  rowSelectAll: PropTypes.object,
  onSelectAll: PropTypes.func
};

Table.defaultProps = {
  fixedHeader: true,
  id: "table",
  rowRenderer: <DefaultRowRenderer />
};

export default withStyles(styles)(Table);
