import React, { Component } from "react";
import { connect } from "react-redux";
import withStyles from "@material-ui/styles/withStyles";

import {
  Edit,
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

const inputOptions = sm => ({
  xs: 12,
  sm,
  fullWidth: true,
  options: { fullWidth: true }
});

const styles = {};

class ActivityEdit extends Component {
  format = record => {
    if (record && record.notify !== null) {
      const { notify, ...rest } = record;
      return { ...rest, notify: notify.split(",").map(Number) };
    } else {
      return record;
    }
  };

  parse = record => {
    if (record && record.notify !== null) {
      const { notify, ...rest } = record;
      return { ...rest, notify: notify.join() }; // Parsing modified record to the API
    } else {
      return record;
    }
  };
  render() {
    const { departments, classes, dispatch, activity, ...props } = this.props;

    return (
      <Edit
        cardWrapper={false}
        record={{ subActivityList: [{}] }}
        parse={this.parse}
        format={this.format}
        {...props}
      >
        <MultiCardForm redirect="home">
          <FormCard title="Activity Details">
            <TextInput
              source="name"
              validate={[required(), minLength(2)]}
              {...inputOptions(3)}
            />
            {
              <ReferenceInput
                source="departmentId"
                reference="departments"
                {...inputOptions(4)}
                validate={required()}
              >
                <SelectInput optionText="name" />
              </ReferenceInput>
            }

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
                  return null;
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
              style={{ marginLeft: "0.5em", marginTop: "1.5em" }}
            />
            <BooleanInput
              defaultValue={false}
              source="isDefault"
              label="Default Activity"
              style={{ marginTop: "1.5em", marginLeft: "-6em" }}
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
      </Edit>
    );
  }
}

const mapStateToProps = state => ({
  departments:
    state.jazasoft.resources["departments"] &&
    state.jazasoft.resources["departments"].data
});

export default connect(mapStateToProps)(withStyles(styles)(ActivityEdit));
