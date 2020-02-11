import React, { Component } from "react";
import { connect } from "react-redux";

import withStyles from "@material-ui/styles/withStyles";

import {
  Create,
  MultiCardForm,
  FormCard,
  TextInput,
  ReferenceInput,
  SelectInput,
  BooleanInput,
  ArrayInput,
  SimpleFormIterator,
  required,
  minLength,
  FormDataConsumer,
  SelectArrayInput
} from "jazasoft";
import hasPrivilege from "../../utils/hasPrivilege";
import Forbidden from "../../components/Forbidden";

const inputOptions = sm => ({
  xs: 12,
  sm,
  fullWidth: true,
  options: { fullWidth: true }
});

const styles = {};

class ActivityCreate extends Component {
  parse = record => {
    const { notify, ...rest } = record;
    return { ...rest, notify: notify && notify.join() }; // Parsing modified record to the API
  };

  render() {
    const { roles, hasAccess, history, departments, dispatch, ...props } = this.props;
    if(!hasPrivilege(roles, hasAccess, "activty", "write")){
      return <Forbidden history={history} />
    }
    return (
      <Create
        title="Generate Activity and subactivities"
        cardWrapper={false}
        record={{ subActivityList: [{}] }}
        parse={this.parse}
        {...props}
      >
        <MultiCardForm redirect="home">
          <FormCard title="Activity Details">
            <TextInput
              source="name"
              validate={[required(), minLength(2)]}
              {...inputOptions(3)}
            />

            <ReferenceInput
              source="departmentId"
              reference="departments"
              {...inputOptions(4)}
              validate={required()}
            >
              <SelectInput optionText="name" />
            </ReferenceInput>
            <FormDataConsumer {...inputOptions(5)}>
              {({ formData }) => {
                if (formData.departmentId) {
                  let selectedDeptId = formData && formData.departmentId;
                  let totalDepartmentList = Object.keys(departments).map(
                    e => departments[e]
                  );
                  const rDepartments =
                    formData.departmentId &&
                    totalDepartmentList.filter(e => selectedDeptId !== e.id);
                  const choices =
                    rDepartments &&
                    Object.values(rDepartments).map(({ id, name }) => ({
                      id,
                      name
                    }));

                  return (
                    <SelectArrayInput
                      source="notify"
                      label="Notify Departments"
                      optionText="name"
                      choices={choices}
                      {...inputOptions(5)}
                    />
                  );
                } else {
                  return (
                    <SelectArrayInput
                      source="notify"
                      label="Notify Departments"
                      optionText="name"
                      choices={[]}
                      {...inputOptions(5)}
                    />
                  );
                }
              }}
            </FormDataConsumer>
            <TextInput
              source="delayReason"
              validate={[minLength(2)]}
              {...inputOptions(4)}
            />
            <BooleanInput
              defaultValue={false}
              source="cLevel"
              label="C Level"
              style={{ marginLeft: "2em", marginTop: "1.5em" }}
            />
            <BooleanInput
              defaultValue={false}
              source="isDefault"
              label="Default Activity"
              style={{marginTop: "1.5em", marginLeft: "-3em", marginRight: "-2em" }}
            />
            <BooleanInput
              defaultValue={false}
              source="overridable"
              label="Overridable"
              style={{ marginTop: "1.5em",}}
            />
          </FormCard>
          <FormCard title="Subactivities">
            <ArrayInput
              label="Subactivity List"
              source="subActivityList"
              xs={12}
              fullWidth={true}
            >
              <SimpleFormIterator>
                <TextInput
                  label="Name"
                  source="name"
                  validate={[required(), minLength(2)]}
                  {...inputOptions(4)}
                />
                <TextInput
                  label="Description"
                  source="desc"
                  {...inputOptions(8)}
                />
              </SimpleFormIterator>
            </ArrayInput>
          </FormCard>
        </MultiCardForm>
      </Create>
    );
  }
}

const mapStateToProps = state => ({
  departments:
    state.jazasoft.resources["departments"] &&
    state.jazasoft.resources["departments"].data
});

export default connect(mapStateToProps)(withStyles(styles)(ActivityCreate));
