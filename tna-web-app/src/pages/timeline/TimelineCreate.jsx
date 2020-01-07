import React, { Component } from "react";
import { connect } from "react-redux";
import isEqual from "lodash/isEqual";

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
  SAVING_START,
  SAVING_END
} from "jazasoft";
import { SimpleForm } from "jazasoft/lib/mui/form/SimpleForm";

import CardHeader from "../../components/CardHeader";
import { dataProvider } from "../../App";

const inputOptions = sm => ({
  xs: 12,
  sm,
  fullWidth: true,
  options: { fullWidth: true }
});

const keysActivity = [
  "leadTimeNormal",
  "leadTimeOptimal",
  "timeFrom",
  "subActivityList"
];

const parse = values => {
  const { name, tnaType, buyerId, ...activity } = values;
  let tActivityList = [];
  console.log({ values });
  Object.keys(activity).forEach(key => {
    const activityId = key.split("-")[0];
    const k = key.split("-")[1];
    if (keysActivity.includes(k)) {
      let tActivity = tActivityList.find(e => e.activityId === activityId);
      let value = activity[key];
      if (k === "subActivityList" && value) {
        value = value.map(({ subActivityId, leadTimeNormal }) => ({
          subActivityId,
          leadTimeNormal
        }));
      }
      if (tActivity) {
        tActivity[k] = value;
      } else {
        tActivityList.push({ activityId, [k]: value });
      }
    }
  });
  const timeline = {
    name,
    buyerId,
    tnaType,
    tActivityList
  };

  return timeline;
};

const format = timeline => {
  const { tActivityList, ...rest } = timeline;

  let tActivities = {};
  tActivityList.forEach(tActivity => {
    tActivities = {
      ...tActivities,
      ...Object.keys(tActivity).reduce(
        (acc, key) => ({
          ...acc,
          [`${tActivity.id}-${key}`]:
            key === "subActivityList"
              ? [...tActivity[key].map(e => ({ ...e, subActivityId: e.id }))]
              : tActivity[key]
        }),
        {}
      )
    };
  });

  return { ...rest, ...tActivities };
};

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

const SelectDialog = ({ open, onClose, data, onSelect }) => (
  <Dialog onClose={onClose} open={open} maxWidth="xs" fullWidth>
    <DialogTitle>Activities</DialogTitle>
    <Divider />
    <List>
      {data.map((activity, idx) => (
        <ListItem divider button onClick={_ => onSelect(activity)} key={idx}>
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

class TimelineCreate extends Component {
  state = {
    activityList: [],
    rActivityList: [],
    initialValues: {},
    dialogActive: false,
    expanded: false
  };

  componentDidMount() {
    this.init();
    this.props.dispatch(crudGetList("activities"));
  }

  componentWillReceiveProps(nextProps) {
    if (!isEqual(this.props.activities, nextProps.activities)) {
      this.init(nextProps);
    }
  }

  init = (props = this.props) => {
    const { activities } = props;

    const activityList = activities
      ? Object.keys(activities).map(id => activities[id])
      : [];
    const initialValues = format({ tActivityList: activityList });
    this.setState({ activityList, initialValues });
  };

  onRemoveActivity = activity => () => {
    let activityList = this.state.activityList.filter(
      e => e.id !== activity.id
    );
    this.setState({ activityList });
  };

  onAddActivity = () => {
    const { activities } = this.props;
    const { activityList } = this.state;
    const totalActivityList = Object.keys(activities).map(id => activities[id]);
    const selectedIds = activityList.map(e => e.id);
    const rActivityList = totalActivityList.filter(
      e => !selectedIds.includes(e.id)
    );
    this.setState({ dialogActive: true, rActivityList });
  };

  onSubmit = handleSubmit => {
    handleSubmit(values => {
      this.createTimeline(parse(values));
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

  onValidate = (values, props) => {
  };

  onSelect = activity => {
    let activityList = this.state.activityList.slice();
    activityList.push(activity);
    this.setState({ activityList, dialogActive: false });
  };

  // Expansion bar control
  handleChange = panel => (event, isExpanded) => {
    this.setState({ expanded: isExpanded ? panel : false });
  };

  render() {
    const { classes } = this.props;
    const {
      dialogActive,
      activityList,
      rActivityList,
      initialValues,
      expanded
    } = this.state;

    return (
      <div>
        <PageHeader title="Create Timeline" />

        <SelectDialog
          open={dialogActive}
          onClose={() => this.setState({ dialogActive: false })}
          data={rActivityList}
          onSelect={this.onSelect}
        />

        <div className={classes.container}>
          <WithReduxForm
            initialValues={initialValues}
            validate={this.onValidate}
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
                <Card className={classes.card}>
                  <div className={classes.panelBtn}>
                    <CardHeader title="Activities" />
                    <Button
                      showLabel={false}
                      label="Add Activity"
                      className={classes.addBtn}
                      onClick={this.onAddActivity}
                    >
                      <AddIcon />
                    </Button>
                  </div>

                  <Divider />

                  <Paper className={classes.activitiesField}>
                    {activityList
                      .sort((a, b) => a.serialNo - b.serialNo)
                      .map((activity, idx) => (
                        <ExpansionPanel
                          key={idx}
                          expanded={expanded === activity.id}
                          onChange={this.handleChange(activity.id)}
                        >
                          <ExpansionPanelSummary
                            expandIcon={<ExpandMoreIcon />}
                          >
                            <div className={classes.panelBtn}>
                              <Typography className={classes.heading}>
                                {activity.name}
                              </Typography>
                              <Button
                                showLabel={false}
                                label="Remove Activity"
                                onClick={this.onRemoveActivity(activity)}
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
                                source={`${activity.id}-leadTimeNormal`}
                                label="Lead Time Normal"
                                {...inputOptions(4)}
                              />
                              <NumberInput
                                source={`${activity.id}-leadTimeOptimal`}
                                label="Lead Time Optimal"
                                {...inputOptions(4)}
                              />
                              <RadioButtonGroupInput
                                className={classes.radioBtn}
                                source={`${activity.id}-timeFrom`}
                                label="Time From"
                                choices={[
                                  { id: "O", name: "Order Date" },
                                  { id: "E", name: "Ex-Factory Date" }
                                ]}
                                {...inputOptions(4)}
                              />
                              <ArrayInput
                                label="Subactivity List"
                                source={`${activity.id}-subActivityList`}
                                {...inputOptions(12)}
                              >
                                <SimpleFormIterator>
                                  <SelectInput
                                    source="subActivityId"
                                    label="Sub Activities"
                                    choices={activity.subActivityList}
                                    {...inputOptions(6)}
                                  />
                                  <NumberInput
                                    source="leadTimeNormal"
                                    label="Lead Time Normal"
                                    {...inputOptions(6)}
                                  />
                                </SimpleFormIterator>
                              </ArrayInput>
                            </SimpleForm>
                          </ExpansionPanelDetails>
                        </ExpansionPanel>
                      ))}
                  </Paper>
                </Card>

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
