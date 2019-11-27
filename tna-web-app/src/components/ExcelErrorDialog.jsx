import React from "react";
import PropTypes from "prop-types";

import Button from "@material-ui/core/Button";
import Dialog from "@material-ui/core/Dialog";
import DialogContent from "@material-ui/core/DialogContent";
import DialogTitle from "@material-ui/core/DialogTitle";
import DialogActions from "@material-ui/core/DialogActions";

import Table from "jazasoft/lib/mui/components/Table";

const defaultColumns = [
  { dataKey: "row", title: "Row" },
  { dataKey: "column", title: "Column" },
  { dataKey: "message", title: "Error Message" }
];

const ErrorDialog = ({ open, isExcelError, columns, errors, onClose, maxWidth = "md" }) => {
  return (
    <Dialog id="error-dialog-container" fullWidth={true} maxWidth={maxWidth} open={open} onClose={onClose}>
      <DialogTitle id="error-dialog-title">Errors</DialogTitle>
      <DialogContent id="error-dialog-content">
        {errors && (
          <Table columns={columns} rows={errors.map(e => ({ ...e, row: isExcelError ? `Row ${e.row}` : e.row }))} />
        )}
      </DialogContent>
      <DialogActions id="dialog-actions" style={{ padding: "1em" }}>
        <Button onClick={onClose} color="primary">
          Close
        </Button>
      </DialogActions>
    </Dialog>
  );
};

ErrorDialog.propTypes = {
  columns: PropTypes.arrayOf(PropTypes.object),
  errors: PropTypes.arrayOf(PropTypes.object),
  open: PropTypes.bool,
  onClose: PropTypes.func,
  isExcelError: PropTypes.bool
};

ErrorDialog.defaultProps = {
  columns: defaultColumns,
  isExcelError: true
};
export default ErrorDialog;
