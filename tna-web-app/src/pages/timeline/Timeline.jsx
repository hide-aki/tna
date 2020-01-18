import React from "react";
import { withStyles } from "@material-ui/styles";
import {
  List,
  Datagrid,
  TextField,
  EditButton,
  ReferenceField,
  DeleteButton,
  ShowButton
} from "jazasoft";

import hasPrivilege from "../../utils/hasPrivilege";

const homeStyle = theme => ({
  button: {
    width: theme.spacing(2)
  }
});

export default withStyles(homeStyle)(({ classes, ...props }) => {
  const { roles, hasAccess } = props;
  return (
    <List {...props}>
      <Datagrid>
        <TextField label="Name" source="name" />
        <ReferenceField source="buyerId" reference="buyers">
          <TextField label="Buyer" source="name" />
        </ReferenceField>

        <TextField label="TNA Type" source="tnaType" />
        <ShowButton cellClassName={classes.button} />
        {hasPrivilege(roles, hasAccess, "timeline", "update") && (
          <EditButton cellClassName={classes.button} />
        )}
        {hasPrivilege(roles, hasAccess, "timeline", "delete") && (
          <DeleteButton cellClassName={classes.button} />
        )}
      </Datagrid>
    </List>
  );
});
