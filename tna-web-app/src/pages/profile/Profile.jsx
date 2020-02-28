import React, { Component } from "react";
import { connect } from "react-redux";
import compose from "recompose/compose";

import Card from "@material-ui/core/Card";
import Grid from "@material-ui/core/Grid";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemSecondaryAction from "@material-ui/core/ListItemSecondaryAction";
import ListItemText from "@material-ui/core/ListItemText";
import Typography from "@material-ui/core/Typography";
import Dialog from "@material-ui/core/Dialog";
import DialogContent from "@material-ui/core/DialogContent";
import DialogTitle from "@material-ui/core/DialogTitle";
import withStyles from "@material-ui/core/styles/withStyles";
import ActionCancel from "@material-ui/icons/Cancel";

import {
  PageHeader,
  PageFooter,
  Button,
  SimpleForm,
  TextInput,
  TextField,
  SaveButton,
  changePassword,
  ReferenceField,
  ReferenceArrayField
} from "jazasoft";

import SingleFieldListCsv from "../../components/SingleFieldListCsv";

const validate = values => {
  const errors = {};

  if (!values.oldPassword) {
    errors.oldPassword = "Old Password Required.";
  }

  if (!values.newPassword) {
    errors.newPassword = "New Password Required.";
  } else if (values.newPassword.length < 6) {
    errors.newPassword = "The password is too short. It must be atleast 6 characters";
  }

  if (!values.confirmNewPassword) {
    errors.confirmNewPassword = "Confirm New Password Required.";
  }

  if (values.newPassword !== values.confirmNewPassword) {
    errors.confirmNewPassword = "Password do not match.";
  }

  return errors;
};

const styles = theme => ({
  content: {
    marginLeft: theme.spacing(2),
    marginRight: theme.spacing(2),
    marginBottom: theme.spacing(2),
    padding: theme.spacing(2)
  }
});

const FormFooter = ({ onCancel, ...props }) => (
  <PageFooter>
    <SaveButton label="Submit" {...props} />
    <Button label="Cancel" variant="contained" onClick={onCancel}>
      <ActionCancel />
    </Button>
  </PageFooter>
);

class Profile extends Component {
  state = {
    dialogActive: false
  };

  closeDialog = () => {
    this.setState({ dialogActive: false });
  };

  componentWillReceiveProps(nextProps) {
    if (this.props.authState.busy && !nextProps.authState.busy) {
      this.setState({ dialogActive: false });
    }
  }

  onSubmit = values => {
    const { username, clientId } = this.props.authState;
    this.props.changePassword({ ...values, username, tenantId: clientId });
  };

  render() {
    const { authState } = this.props;

    let data = [
      { label: "Full Name", value: authState.fullName },
      { label: "User Name", value: authState.username },
      { label: "Email", value: authState.email || "-" },
      { label: "Mobile", value: authState.mobile },
      { label: "Roles", value: authState.roleList.map(r => r.role && r.role.name).join(", ") }
    ];
    const departmentId = authState.departmentId ? Number(authState.departmentId) : null;
    if (departmentId) {
      data.push({
        label: "Department",
        value: (
          <span>
            <ReferenceField record={{ departmentId }} source="departmentId" reference="departments">
              <TextField source="name" />
            </ReferenceField>
          </span>
        )
      });
    }

    const teamId = authState.teamId ? Number(authState.teamId) : null;
    if (teamId) {
      data.push({
        label: "Team",
        value: (
          <span>
            <ReferenceField record={{ teamId }} source="teamId" reference="teams">
              <TextField source="name" />
            </ReferenceField>
          </span>
        )
      });
    }

    const buyerIds = authState && authState.buyerId && authState.buyerId.map(Number);
    if (buyerIds && buyerIds.length > 0) {
      data.push({
        label: "Buyer Access",
        value: (
          <span>
            <ReferenceArrayField record={{ buyerIds }} source="buyerIds" reference="buyers">
              <SingleFieldListCsv source="name" />
            </ReferenceArrayField>
          </span>
        )
      });
    }

    const { classes } = this.props;
    return (
      <div>
        <PageHeader title="My Profile" />
        <Card className={classes.content} elavation={4} square={true}>
          <Grid container>
            <Grid item xs={12} sm={8}>
              <List>
                {data.length > 0 &&
                  data.map(e => (
                    <ListItem key={e.label} button>
                      <ListItemText secondary={e.label} secondaryTypographyProps={{ variant: "subtitle1" }} />
                      <ListItemSecondaryAction>
                        <Typography component="div">{e.value}</Typography>
                      </ListItemSecondaryAction>
                    </ListItem>
                  ))}
              </List>
            </Grid>
          </Grid>

          <PageFooter>
            <Button variant="contained" color="primary" label="Change Password" onClick={() => this.setState({ dialogActive: true })} />
          </PageFooter>
        </Card>

        <div>
          <Dialog open={this.state.dialogActive} onClose={this.closeDialog} aria-labelledby="form-dialog-title">
            <DialogTitle id="form-dialog-title">Change Password</DialogTitle>
            <DialogContent style={{ width: 400 }}>
              <SimpleForm onSubmit={this.onSubmit} validate={validate} footer={<FormFooter onCancel={this.closeDialog} />}>
                <TextInput source="oldPassword" type="password" xs={12} fullWidth options={{ fullWidth: true }} />
                <TextInput source="newPassword" type="password" xs={12} fullWidth options={{ fullWidth: true }} />
                <TextInput source="confirmNewPassword" type="password" xs={12} fullWidth options={{ fullWidth: true }} />
              </SimpleForm>
            </DialogContent>
          </Dialog>
        </div>
      </div>
    );
  }
}

export default compose(
  connect(state => ({ authState: state.jazasoft.auth }), { changePassword }),
  withStyles(styles)
)(Profile);
