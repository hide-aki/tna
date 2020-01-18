import React, { Component } from "react";
import { connect } from "react-redux";
import isEqual from "lodash/isEqual";

//Material-UI
import { withStyles } from "@material-ui/styles";
import {
  Typography,
  ExpansionPanelSummary,
  ExpansionPanelDetails,
  ExpansionPanel,
  Card,
  Divider,
  Paper,
  Dialog,
  DialogTitle,
  List,
  ListItem,
  ListItemText
} from "@material-ui/core";
import ExpandMoreIcon from "@material-ui/icons/ExpandMore";
import RemoveIcon from "@material-ui/icons/HighlightOff";
import SaveIcon from "@material-ui/icons/Save";
import AddIcon from "@material-ui/icons/Add";

import {
  PageHeader,
  RadioButtonGroupInput,
  PageFooter,
  TextInput,
  NumberInput,
  ReferenceInput,
  SelectInput,
  ArrayInput,
  SimpleFormIterator,
  required,
  WithReduxForm,
  Button,
  BackButton,
  crudGetList,
  RestMethods,
  minValue,
  SAVING_START,
  SAVING_END
} from "jazasoft";
import { SimpleForm } from "jazasoft/lib/mui/form/SimpleForm";
import CardHeader from "../../components/CardHeader";
import { dataProvider } from "../../App";
import { Field, FieldArray } from "redux-form";

// Styling
const homeStyle = theme => ({
  container: {
    margin: theme.spacing(3)
  },
  panelBtn: {
    width: "100%",
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center"
  },
  card: {
    margin: "1em 0"
  },
  heading: {
    fontSize: theme.typography.pxToRem(15),
    fontWeight: theme.typography.fontWeightRegular
  },
  timelineField: {
    padding: "1.5em"
  },
  activitiesField: {
    padding: "1em"
  },
  addBtn: {
    marginRight: "1em"
  },
  backBtn: {
    marginLeft: "1em"
  },
  form: {
    width: "100%"
  },
  radioBtn: {
    marginLeft: "3em",
    marginTop: "3em",
    "&>*": {
      display: "flex",
      flexDirection: "row"
    }
  }
});

const inputOptions = sm => ({
  xs: 12,
  sm,
  fullWidth: true
});

// Creating timeline structure for Redux-Fields
const format = activityList => {
  let timeline = {
    tActivityList: activityList.map(({ id, name, subActivityList }) => ({
      activityId: id,
      name,
      tSubActivityList: subActivityList.map(({ id, name }) => ({
        subActivityId: id,
        name
      }))
    }))
  };

  return timeline;
};

// To populate Activity name on Expansion Panel
const TextField = ({ className, input: { value } }) => (
  <Typography className={className}>{value}</Typography>
);

// Add Activity Dialog box Content
const SelectDialog = ({ open, onClose, data, fields, onSelect }) => {
  return (
    <Dialog onClose={onClose} open={open} maxWidth="xs" fullWidth>
      <DialogTitle>Activities</DialogTitle>
      <Divider />
      <List>
        {data.map((activity, idx) => (
          <ListItem
            divider
            button
            onClick={_ => onSelect(fields, activity)}
            key={idx}
          >
            <ListItemText primary={activity.name} />
          </ListItem>
        ))}
        {data.length === 0 && (
          <div
            style={{
              display: "flex",
              justifyContent: "center",
              padding: "1em"
            }}
          >
            No data available
          </div>
        )}
      </List>
    </Dialog>
  );
};

// Activity Field Array
const renderActivities = ({
  fields,
  activities,
  classes,
  expanded,
  handleExpansionPanelChange,
  onAddActivity,
  onRemoveActivity
}) => {
  return (
    <Card className={classes.card}>
      <div className={classes.panelBtn}>
        <CardHeader title="Activities" />
        <Button
          showLabel={false}
          label="Add Activity"
          className={classes.addBtn}
          onClick={_ => onAddActivity(fields)}
        >
          <AddIcon />
        </Button>
      </div>
      <Divider />
      <Paper className={classes.activitiesField}>
        {fields.map((activity, idx) => {
          const activityObj =
            activities &&
            fields.get(idx) &&
            activities[fields.get(idx).activityId];

          return (
            <ExpansionPanel
              key={idx}
              expanded={expanded === `${activity}.id`}
              onChange={handleExpansionPanelChange(`${activity}.id`)}
            >
              <ExpansionPanelSummary expandIcon={<ExpandMoreIcon />}>
                <div className={classes.panelBtn}>
                  <Field
                    name={`${activity}.name`}
                    component={TextField}
                    className={classes.heading}
                  />

                  <Button
                    showLabel={false}
                    label="Remove Activity"
                    onClick={_ => onRemoveActivity(fields, idx, activity)}
                  >
                    <RemoveIcon />
                  </Button>
                </div>
              </ExpansionPanelSummary>
              <ExpansionPanelDetails>
                <SimpleForm
                  component="div"
                  className={classes.form}
                  footer={false}
                >
                  <NumberInput
                    source={`${activity}.leadTimeNormal`}
                    label="Lead Time Normal"
                    {...inputOptions(4)}
                    validate={[required(1), minValue(1)]}
                  />
                  <NumberInput
                    source={`${activity}.leadTimeOptimal`}
                    label="Lead Time Optimal"
                    {...inputOptions(4)}
                    validate={[required(), minValue(1)]}
                  />
                  <RadioButtonGroupInput
                    className={classes.radioBtn}
                    source={`${activity}.timeFrom`}
                    label="Time From"
                    choices={[
                      { id: "O", name: "Order Date" },
                      { id: "E", name: "Ex-Factory Date" }
                    ]}
                    {...inputOptions(4)}
                    validate={required()}
                  />
                  <ArrayInput
                    label="Subactivity List"
                    source={`${activity}.tSubActivityList`}
                    {...inputOptions(12)}
                  >
                    <SimpleFormIterator>
                      {activityObj && activityObj.subActivityList && (
                        <SelectInput
                          source="subActivityId"
                          label="Sub Activities"
                          choices={activityObj.subActivityList.map(
                            ({ id, name }) => ({
                              id: id,
                              name
                            })
                          )}
                          {...inputOptions(6)}
                          validate={required()}
                        />
                      )}

                      <NumberInput
                        source="leadTimeNormal"
                        label="Lead Time Normal"
                        {...inputOptions(6)}
                        validate={[required(), minValue(1)]}
                      />
                    </SimpleFormIterator>
                  </ArrayInput>
                </SimpleForm>
              </ExpansionPanelDetails>
            </ExpansionPanel>
          );
        })}
      </Paper>
    </Card>
  );
};

class TimelineCreate extends Component {
  state = {
    activityList: [],
    rActivityList: [],
    initialValues: {},
    dialogActive: false,
    expanded: false
  };

  componentDidMount() {
    this.props.dispatch(crudGetList("activities"));
    this.init();
  }

  componentWillReceiveProps(nextProps) {
    if (!isEqual(this.props.activities, nextProps.activities)) {
      this.init(nextProps);
    }
  }

  init = (props = this.props) => {
    const { activities } = props;
    const activityList = activities
      ? Object.keys(activities)
          .map(id => activities[id])
          .sort((a, b) => a.serialNo - b.serialNo)
      : [];
    const initialValues = format(activityList);
    this.setState({
      activityList,
      initialValues
    });
  };

  onRemoveActivity = (fields, index) => {
    let activityList = this.state.activityList.slice();
    activityList.splice(index, 1);
    this.setState({ activityList: activityList });
    fields.remove(index);
  };

  onAddActivity = fields => {
    const { activities } = this.props;
    const { activityList } = this.state;
    const totalActivityList = Object.keys(activities).map(id => activities[id]);
    const selectedIds = activityList.map(e => e.id);
    const rActivityList = totalActivityList.filter(
      e => !selectedIds.includes(e.id)
    );
    this.setState({ dialogActive: true, rActivityList, fields });
  };

  onSubmit = handleSubmit => {
    handleSubmit(values => {
      this.createTimeline(values);
    })();
  };

  createTimeline = timeline => {
    const options = {
      url: "timelines",
      method: "post",
      data: timeline
    };
    this.props.dispatch({ type: SAVING_START });
    dataProvider(RestMethods.CUSTOM, null, options)
      .then(response => {
        this.props.dispatch({ type: SAVING_END });
        this.props.history.push("/timelines");
      })
      .catch(error => {
        this.props.dispatch({ type: SAVING_END });
      });
  };

  onValidate = values => {
    const errors = {};
    const tActivityList = [];

    values &&
      Object.values(values)[0] &&
      Object.values(values)[0].forEach((activity, activityIdx) => {
        const tActivity = {};
        if (activity.leadTimeOptimal >= activity.leadTimeNormal) {
          tActivity.leadTimeOptimal =
            "Value should be less than Lead Time Normal";
          tActivityList[activityIdx] = tActivity;
        }
        const tSubActivityList = [];

        let sortedSubActivityList =
          activity.tSubActivityList &&
          activity.tSubActivityList.map(a => a.subActivityId);
        activity.tSubActivityList.forEach((subActivity, subActivityIdx) => {
          const tSubActivity = {};
          if (subActivity.leadTimeNormal > activity.leadTimeNormal) {
            tSubActivity.leadTimeNormal =
              "Value should be less than Activity's Lead Time Normal";
            tSubActivityList[subActivityIdx] = tSubActivity;
          } else if (
            sortedSubActivityList.some(
              (a, index) => sortedSubActivityList.indexOf(a) !== index
            )
          ) {
            tSubActivity.subActivityId = "Subactivities should not be same";
            tSubActivityList[subActivityIdx] = tSubActivity;
          }
        });
        if (tSubActivityList.length) {
          tActivity.tSubActivityList = tSubActivityList;
          tActivityList[activityIdx] = tActivity;
        }
      });
    if (tActivityList.length) {
      errors.tActivityList = tActivityList;
    }

    return errors;
  };

  onSelect = (fields, activity) => {
    let activityList = this.state.activityList.slice();
    activityList.push(activity);
    this.setState({ activityList, dialogActive: false });
    let newActivity = {
      activityId: activity.id,
      name: activity.name,
      tSubActivityList: activity.subActivityList.map(({ id, name }) => ({
        subActivityId: id,
        name
      }))
    };
    fields.push(newActivity);
  };

  // Expansion bar control
  handleChange = panel => (event, isExpanded) => {
    this.setState({ expanded: isExpanded ? panel : false });
  };

  render() {
    const { classes, activities } = this.props;
    const {
      dialogActive,
      rActivityList,
      fields,
      initialValues,
      expanded
    } = this.state;

    return (
      <div>
        <PageHeader title="Create Timeline" />
        <SelectDialog
          open={dialogActive}
          onClose={() => this.setState({ dialogActive: false, fields: null })}
          data={rActivityList}
          onSelect={this.onSelect}
          fields={fields}
        />
        <div className={classes.container}>
          <WithReduxForm
            initialValues={initialValues}
            validate={this.onValidate}
            onChange={this.onChange}
          >
            {({ handleSubmit }) => (
              <div>
                <Card className={classes.card}>
                  <CardHeader title="Timeline Details" />
                  <Divider />
                  <div className={classes.timelineField}>
                    <SimpleForm component="div" footer={false}>
                      <ReferenceInput
                        source="buyerId"
                        reference="buyers"
                        {...inputOptions(4)}
                        validate={required()}
                      >
                        <SelectInput optionText="name" />
                      </ReferenceInput>
                      <TextInput
                        source="name"
                        validate={[required()]}
                        {...inputOptions(4)}
                      />
                      <SelectInput
                        source="tnaType"
                        label="TNA Type"
                        choices={[
                          { id: "Backward", name: "Backward" },
                          { id: "Forward", name: "Forward" }
                        ]}
                        validate={[required()]}
                        {...inputOptions(4)}
                      />
                    </SimpleForm>
                  </div>
                </Card>

                <FieldArray
                  name="tActivityList"
                  component={renderActivities}
                  activities={activities}
                  classes={classes}
                  expanded={expanded}
                  handleExpansionPanelChange={this.handleChange}
                  onAddActivity={this.onAddActivity}
                  onRemoveActivity={this.onRemoveActivity}
                />

                <PageFooter>
                  <Button
                    label="Save"
                    variant="contained"
                    color="primary"
                    onClick={_ => this.onSubmit(handleSubmit)}
                  >
                    <SaveIcon />
                  </Button>
                  <BackButton
                    style={{ marginLeft: "1em" }}
                    variant="contained"
                  />
                </PageFooter>
              </div>
            )}
          </WithReduxForm>
        </div>
      </div>
    );
  }
}

const mapStateToProps = state => ({
  activities:
    state.jazasoft.resources["activities"] &&
    state.jazasoft.resources["activities"].data,
  saving: state.jazasoft.saving
});

export default connect(mapStateToProps)(withStyles(homeStyle)(TimelineCreate));
