import React from "react";
import { connect } from "react-redux";
//material ui
import withStyles from "@material-ui/styles/withStyles";
import Paper from "@material-ui/core/Paper";
//jazasoft
import {
  PageHeader,
  SimpleForm,
  showNotification,
  FileInput,
  FileField,
  RestMethods,
  FETCH_START,
  FETCH_END,
  SAVING_START,
  SAVING_END,
  required
} from "jazasoft";
// read excel file
import readXlsxFile from "read-excel-file";
// local
import ExcelErrorDialog from "../../components/ExcelErrorDialog";
//helper
import { authDataProvider, dataProvider } from "../../App";
import handleError from "../../utils/handleError";
import { REGEX_USERNAME, REGEX_EMAIL, REGEX_MOBILE } from "../../utils/regex";

const styles = {
  root: {
    margin: "1.5em",
    padding: "1.5em"
  }
};

class UserUpload extends React.Component {
  state = {
    errors: null,
    dialogActive: false,
    roleList: []
  };

  componentDidMount() {
    this.fetchRoles();
  }

  fetchRoles = () => {
    const options = {
      tenantId: this.props.authState && this.props.authState.clientId,
      url: `roles`,
      method: "get"
    };
    this.props.dispatch({ type: FETCH_START });

    authDataProvider(RestMethods.CUSTOM, undefined, options)
      .then(response => {
        if (response.status === 200) {
          const roleList = response.data && response.data.content;
          this.setState({ roleList: roleList ? roleList.map(e => e.roleId) : [] });
        }
        this.props.dispatch({ type: FETCH_END });
      })
      .catch(err => {
        handleError(err, this.props.dispatch);
        this.props.dispatch({ type: FETCH_END });
      });
  };

  onSubmit = async values => {
    const { file } = values;

    try {
      let { rows, errors } = await readXlsxFile(file.rawFile, {
        schema: this.getSchema()
      });
      if (errors && errors.length > 0) {
        const errorList = errors.map(({ error, row, column }) => ({ row, column, message: error }));
        this.setState({ errors: errorList, dialogActive: false, dialogName: null, isExcelError: true });
        return;
      }
      console.log({ rows });
      //validate
      errors = [];
      rows.forEach((row, idx) => {
        if (!row.fullName) {
          errors.push({ row: idx + 1, column: "FULL NAME", message: "Full Name required." });
        }
        if (!row.username) {
          errors.push({ row: idx + 1, column: "USERNAME", message: "Username required." });
        }
        if (!row.mobile) {
          errors.push({ row: idx + 1, column: "MOBILE", message: "Mobile number required." });
        }
        if (!row.roles) {
          errors.push({ row: idx + 1, column: "ROLES", message: "Roles required." });
        }
      });

      if (errors.length > 0) {
        this.setState({ errors });
        return;
      }

      // First Upload Users in Auth Server
      let options = {
        tenantId: this.props.authState && this.props.authState.clientId,
        url: `users/batchSave`,
        method: "post",
        data: rows
      };

      this.props.dispatch({ type: FETCH_START });
      this.props.dispatch({ type: SAVING_START });

      let response = await authDataProvider(RestMethods.CUSTOM, undefined, options);
      const authUsers = response.data;

      // Then Upload User in App
      const appUsers = rows.map(user => {
        const au = authUsers.find(u => u.username === user.username);
        return {
          ...user,
          id: au && au.id,
          roles: au.roleList && au.roleList.map(r => r.role && r.role.roleId).join(", ")
        };
      });

      options = { ...options, data: appUsers };
      response = await dataProvider(RestMethods.CUSTOM, undefined, options);

      this.props.dispatch({ type: FETCH_END });
      this.props.dispatch({ type: SAVING_END });
      this.props.dispatch(showNotification("Users uploaded successfully."));
      this.props.history.push("/users");
    } catch (err) {
      console.log(err);

      if (err.status === 400 && err.errors && Array.isArray(err.errors)) {
        this.setState({ errors: err.errors });
      } else {
        handleError(err, this.props.dispatch);
      }
      this.props.dispatch({ type: FETCH_END });
      this.props.dispatch({ type: SAVING_END });
    }
  };

  getSchema = () => {
    const { roleList } = this.state;
    return {
      USERNAME: {
        prop: "username",
        type: String,
        validate: this.validate({ column: "username" })
      },
      "FULL NAME": {
        prop: "fullName",
        type: String
      },
      EMAIL: {
        prop: "email",
        type: String,
        validate: this.validate({ column: "email" })
      },
      MOBILE: {
        prop: "mobile",
        type: String,
        validate: this.validate({ column: "mobile" })
      },
      ROLES: {
        prop: "roles",
        type: String,
        validate: this.validate({ column: "roles", roleList })
      }
    };
  };

  validate = ({ column, roleList }) => value => {
    value = value && String(value);
    if (column === "username") {
      if (!REGEX_USERNAME.test(value.trim())) {
        throw new Error(
          `Invalid value for Username. Username should consists of Alphabets, numbers and (hyphen, underscore, dot) special characters only and starting with Alphabet`
        );
      }
    } else if (column === "email") {
      if (!REGEX_EMAIL.test(value.trim())) {
        throw new Error(`Invalid value for Email.`);
      }
    } else if (column === "mobile") {
      if (!REGEX_MOBILE.test(value.trim())) {
        throw new Error(`Invalid value for Mobile. Mobile number must be 10 digit numeric value`);
      }
    } else if (column === "roles" && value) {
      let roles = value.toLowerCase().split(",");
      const invalidRoles = roles.filter(role => !roleList.includes(role.trim()));
      if (invalidRoles.length > 0) {
        throw new Error(`Invalid Roles - ${invalidRoles.join(",")}. Accepted roles are: ${roleList.join(", ")}`);
      }
    }
  };

  render() {
    const { classes } = this.props;
    return (
      <div>
        <PageHeader title="Upload Users" />

        <ExcelErrorDialog
          maxWidth="xl"
          open={Array.isArray(this.state.errors)}
          errors={this.state.errors}
          onClose={() => this.setState({ errors: null })}
        />

        <Paper className={classes.root}>
          <SimpleForm onSubmit={this.onSubmit}>
            <FileInput
              source="file"
              label="Excel file"
              accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel"
              xs={12}
              fullWidth
              validate={required()}
            >
              <FileField source="src" title="title" />
            </FileInput>
          </SimpleForm>
        </Paper>
      </div>
    );
  }
}

const mapStateToProps = state => ({
  saving: state.jazasoft.saving,
  authState: state.jazasoft.auth
});

export default connect(mapStateToProps)(withStyles(styles)(UserUpload));
