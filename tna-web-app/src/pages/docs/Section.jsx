import React, { Component } from "react";

import { withStyles } from "@material-ui/core/styles";

import Divider from "@material-ui/core/Divider";
import Dialog from "@material-ui/core/Dialog";
import DialogTitle from "@material-ui/core/DialogTitle";
import DialogContent from "@material-ui/core/DialogContent";
import Breadcrumbs from "@material-ui/core/Breadcrumbs";
import Typography from "@material-ui/core/Typography";
import Link from "@material-ui/core/Link";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemText from "@material-ui/core/ListItemText";

import NavigateNextIcon from "@material-ui/icons/NavigateNext";
import CreateIcon from "mdi-material-ui/Plus";
import ActionCancel from "@material-ui/icons/Cancel";

import { PageFooter, Button, SaveButton, SimpleForm, NumberInput, TextInput, required } from "jazasoft";

import DocsController from "./DocsController";
import DocPage from "../../components/DocPage";
import { Role } from "../../utils/types";

const SubSectionCreateDialog = ({ open, onClose, onSubmit }) => {
  return (
    <Dialog open={open} fullWidth={true} maxWidth="sm" onClose={onClose}>
      <DialogTitle>Create Sub Section</DialogTitle>
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
          <NumberInput source="serialNo" validate={required()} xs={6} options={{ fullWidth: true }} fullWidth />
          <TextInput source="name" validate={required()} xs={6} options={{ fullWidth: true }} fullWidth />
        </SimpleForm>
      </DialogContent>
    </Dialog>
  );
};

const styles = theme => ({
  header: {
    margin: "1em 1.5em",
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center"
  }
});

class Section extends Component {
  state = {
    dialogActive: false
  };

  componentDidMount() {
    this.props.fetchSections();
    this.props.fetchSubSections();
    this.props.fetchTopics(`subSection.section.id==${this.props.match.params.sectionId}`);
  }

  componentWillReceiveProps(nextProps) {
    if (this.props.match.params.sectionId !== nextProps.match.params.sectionId) {
      this.props.fetchTopics(`subSection.section.id==${nextProps.match.params.sectionId}`);
    }
  }

  onSubSectionCreate = values => {
    const sectionId = this.props.match.params.sectionId;
    this.props.saveSubSection({ ...values, sectionId }, _ => this.setState({ dialogActive: false }));
  };

  handleLinkClick = path => e => {
    e.preventDefault();
    this.props.history.push(path);
  };

  render() {
    const { classes, roles, sections = {}, subSections = {}, topics = {}, match, history, location } = this.props;
    const { dialogActive } = this.state;
    const sectionId = match.params.sectionId;
    const section = sections && sections[sectionId];
    const type = location.pathname.includes("/help") ? "help" : "manual";
    const navLinks = Object.keys(sections)
      .map(id => sections[id])
      .sort((a, b) => a.serialNo - b.serialNo)
      .map(({ id, name }) => ({ name, path: `/${type}/${id}` }));

    const subSectionList = Object.keys(subSections)
      .map(id => subSections[id])
      .filter(e => section && e.section.id === section.id);
    const topicList = Object.keys(topics).map(id => topics[id]);
    const headerElement = (
      <div className={classes.header}>
        <Breadcrumbs separator={<NavigateNextIcon fontSize="small" />} aria-label="breadcrumb">
          <Link color="inherit" href={`/${type}`} onClick={this.handleLinkClick(`/${type}`)}>
            {type === "help" ? "Help Center" : "User Manual"}
          </Link>
          <Typography color="textPrimary">{section && section.name}</Typography>
        </Breadcrumbs>
        {roles && roles.includes(Role.MASTER) && (
          <div>
            <Button
              label="Create Sub Section"
              color="secondary"
              variant="contained"
              onClick={_ => this.setState({ dialogActive: true })}
            >
              <CreateIcon />
            </Button>
            <Button
              style={{ marginLeft: "1em" }}
              label="Create Topic"
              color="secondary"
              variant="contained"
              onClick={_ => history.push(`/${type}/topics/create`, { section })}
            >
              <CreateIcon />
            </Button>
          </div>
        )}
      </div>
    );
    return (
      <div>
        <SubSectionCreateDialog
          open={dialogActive}
          onClose={_ => this.setState({ dialogActive: false })}
          onSubmit={this.onSubSectionCreate}
        />

        <DocPage headerElement={headerElement} navTitle={"Sections"} navLinks={navLinks}>
          <div style={{ margin: "1.5em" }}>
            <Typography variant="h5">{section && section.name}</Typography>
          </div>
          {subSectionList.map(({ name, id }) => (
            <div key={id}>
              {subSectionList.length > 1 && (
                <Typography style={{ marginLeft: "1em" }} variant="h6">
                  {name}
                </Typography>
              )}

              {topicList.filter(e => e.subSection && e.subSection.id === id).length === 0 && (
                <Typography style={{ marginLeft: "1.5em" }} color="secondary" variant="subtitle1">
                  No Topic available
                </Typography>
              )}

              <List dense>
                {topicList
                  .filter(e => e.subSection && e.subSection.id === id)
                  .map(({ name, id }) => (
                    <ListItem key={id} onClick={_ => history.push(`/${type}/topics/${id}/view`, { section })}>
                      <ListItemText
                        primary={name}
                        primaryTypographyProps={{ color: "secondary", variant: "subtitle1" }}
                      />
                    </ListItem>
                  ))}
              </List>
            </div>
          ))}
        </DocPage>
      </div>
    );
  }
}

const StyledSection = withStyles(styles)(Section);

export default props => (
  <DocsController {...props}>{controllerProps => <StyledSection {...props} {...controllerProps} />}</DocsController>
);
