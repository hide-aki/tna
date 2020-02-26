import React from "react";
import classnames from "classnames";

import { withStyles } from "@material-ui/core/styles";
import Divider from "@material-ui/core/Divider";
import Typography from "@material-ui/core/Typography";
import Dialog from "@material-ui/core/Dialog";
import DialogTitle from "@material-ui/core/DialogTitle";
import DialogContent from "@material-ui/core/DialogContent";
import Card from "@material-ui/core/Card";
import CardHeader from "@material-ui/core/CardHeader";
import CardActions from "@material-ui/core/CardActions";
import Grid from "@material-ui/core/Grid";

import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemText from "@material-ui/core/ListItemText";

import CreateIcon from "mdi-material-ui/Plus";
import ActionCancel from "@material-ui/icons/Cancel";

import { PageHeader, PageFooter, Button, SaveButton, SimpleForm, NumberInput, TextInput, required } from "jazasoft";

import DocsController from "./DocsController";
import { Role } from "../../utils/types";

const SectionCreateDialog = ({ open, onClose, onSubmit }) => {
  return (
    <Dialog open={open} fullWidth={true} maxWidth="sm" onClose={onClose}>
      <DialogTitle>Create Section</DialogTitle>
      <Divider />
      <DialogContent>
        <SimpleForm
          onSubmit={onSubmit}
          footer={props => (
            <PageFooter>
              <SaveButton {...props} label="Submit" />
              <Button label="Cancel" onClick={onClose} variant="contained">
                <ActionCancel />
              </Button>
            </PageFooter>
          )}
        >
          <NumberInput source="serialNo" validate={required()} xs={6} />
          <TextInput source="name" validate={required()} xs={6} />
        </SimpleForm>
      </DialogContent>
    </Dialog>
  );
};

const SectionCard = ({ section = {}, topics = {}, type, history }) => {
  const topicList = Object.keys(topics)
    .map(id => topics[id])
    .filter(e => e.featured && e.subSection && e.subSection.section && e.subSection.section.id === section.id);
  return (
    <Card style={{ display: "flex", flexDirection: "column", height: "100%" }}>
      <CardHeader title={section.name} onClick={_ => history && history.push(`/${type}/${section.id}`)} />
      <div style={{ display: "flex", flexGrow: 1, padding: 0 }}>
        {topicList.length > 0 && (
          <List dense disablePadding>
            {topicList.map((topic, idx) => (
              <ListItem key={idx} onClick={_ => history.push(`/${type}/topics/${topic.id}/view`, { section })}>
                <ListItemText
                  style={{ padding: 0, margin: 0 }}
                  primary={topic.name}
                  primaryTypographyProps={{ color: "secondary", variant: "subtitle1" }}
                />
              </ListItem>
            ))}
          </List>
        )}
        {!topicList.length && (
          <Typography color="secondary" style={{ marginLeft: "1.5em", marginTop: "1em", marginBottom: "1em" }}>
            No Featured Topic Available
          </Typography>
        )}
      </div>

      <CardActions>
        <Button color="primary" onClick={_ => history && history.push(`/${type}/${section.id}`)}>
          See All Docs
        </Button>
      </CardActions>
    </Card>
  );
};

const styles = theme => ({
  root: {},
  container: {
    margin: theme.spacing(3),
    padding: 0
  },
  card: {
    padding: "0",
    boxShadow: theme.shadows[1],
    backgroundColor: "white"
  },
  marginVertical: {
    margin: "1em 0"
  },
  displayCenter: {
    display: "flex",
    justifyContent: "center"
  }
});

class Help extends React.Component {
  state = {
    dialogActive: false
  };

  componentDidMount() {
    this.props.fetchSections();
    this.props.fetchTopics();
  }

  onSectionCreate = values => {
    const type = this.props.location.pathname.includes("help") ? "Help" : "Manual";
    this.props.saveSection({ ...values, type }, _ => this.setState({ dialogActive: false }));
  };

  render() {
    const { classes, roles, sections, topics, history, location } = this.props;
    const { dialogActive } = this.state;

    const sectionList = Object.keys(sections).map(id => sections[id]);
    const type = location.pathname.includes("help") ? "help" : "manual";
    const title = location.pathname.includes("help") ? "Help Center" : "User Manual";
    return (
      <div className={classnames(classes.root)}>
        <PageHeader title={title}>
          {roles && roles.includes(Role.MASTER) && (
            <Button
              label="Create Section"
              color="secondary"
              variant="contained"
              onClick={_ => this.setState({ dialogActive: true })}
            >
              <CreateIcon />
            </Button>
          )}
        </PageHeader>

        <SectionCreateDialog
          open={dialogActive}
          onSubmit={this.onSectionCreate}
          onClose={_ => this.setState({ dialogActive: false })}
        />

        <div className={classes.container}>
          <Grid container spacing={2} alignItems="stretch">
            {sectionList
              .sort((a, b) => a.serialNo - b.serialNo)
              .map((section, idx) => (
                <Grid key={idx} item xs={12} md={4}>
                  <SectionCard section={section} topics={topics} type={type} history={history} />
                </Grid>
              ))}
          </Grid>
        </div>
      </div>
    );
  }
}

const StyledHelp = withStyles(styles)(Help);

export default props => (
  <DocsController {...props}>{controllerProps => <StyledHelp {...props} {...controllerProps} />}</DocsController>
);
