import React from "react";
import Button from "@material-ui/core/Button";
// import TextField from "@material-ui/core/TextField";
import Dialog from "@material-ui/core/Dialog";
import DialogActions from "@material-ui/core/DialogActions";
import DialogContent from "@material-ui/core/DialogContent";
// import DialogContentText from "@material-ui/core/DialogContentText";
import DialogTitle from "@material-ui/core/DialogTitle";

import Table from "jazasoft/lib/mui/components/Table";

const columnsActivity = [
  { dataKey: "activity", title: "Activity" },
  { dataKey: "completedDate", title: "Completed Date" },
  { dataKey: "delayReason", title: "Delay Reason" },
  { dataKey: "remarks", title: "Remarks" }
];

const columnsSubActivity = [
  { dataKey: "subActivity", title: "Sub Activity" },
  { dataKey: "completedDate", title: "Completed Date" },
  { dataKey: "remarks", title: "Remarks" }
];

const FormDialog = ({ open, onSubmit, onClose }) => {
  const [state, setState] = React.useState({ page: "Activity" });
  return (
    <Dialog open={open} maxWidth="md" fullWidth onClose={onClose} aria-labelledby="form-dialog-title">
      <DialogTitle id="form-dialog-title">{state.page}</DialogTitle>
      <DialogContent>
        <Table columns={state.page === "Activity" ? columnsActivity : columnsSubActivity} rows={[{}]} />
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} color="primary">
          Close
        </Button>
        <Button onClick={onSubmit} color="primary">
          Submit
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default FormDialog;
