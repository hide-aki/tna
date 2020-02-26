import React, { Component } from "react";

import { withStyles } from "@material-ui/core/styles";

import Breadcrumbs from "@material-ui/core/Breadcrumbs";
import Typography from "@material-ui/core/Typography";
import Link from "@material-ui/core/Link";

import NavigateNextIcon from "@material-ui/icons/NavigateNext";

import { SimpleForm, NumberInput, TextInput, SelectInput, BooleanInput, required } from "jazasoft";
import RichTextInput from "jazasoft/lib/mui/input/RichTextInput";

import DocsController from "./DocsController";
import { modules, formats } from "./quillProps";
import { Role } from "../../utils/types";

const inputOptions = sm => ({
  xs: 12,
  sm,
  fullWidth: true,
  options: { fullWidth: true }
});

const styles = theme => ({
  header: {
    margin: "1em 1.5em",
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center"
  },
  container: {
    margin: theme.spacing(3),
    boxShadow: theme.shadows[1],
    backgroundColor: "white",
    padding: "1.5em"
  }
});

class CreateTopic extends Component {
  componentDidMount() {
    this.props.fetchSubSections();
    this.props.fetchTopic(this.props.match.params.id);
  }

  onSubmit = values => {
    this.props.saveOrUpdateTopic("update", values, _ => this.props.history.goBack());
  };

  handleLinkClick = path => e => {
    e.preventDefault();
    this.props.history.push(path);
  };

  render() {
    const { classes, roles, subSections, topics = {}, location, match } = this.props;
    if (!roles || !roles.includes(Role.MASTER)) {
      return <div>Forbidden</div>;
    }
    const section = location.state.section || {};
    const subSectionList = Object.keys(subSections)
      .map(id => subSections[id])
      .filter(e => e.section.id === section.id);
    const type = location.pathname.includes("/help") ? "help" : "manual";
    const topic = topics[match.params.id];
    return (
      <div>
        <div className={classes.header}>
          <Breadcrumbs separator={<NavigateNextIcon fontSize="small" />} aria-label="breadcrumb">
            <Link color="inherit" href={`/${type}`} onClick={this.handleLinkClick(`/${type}`)}>
              {type === "help" ? "Help Center" : "User Manual"}
            </Link>
            <Link
              color="inherit"
              href={`/${type}/${section.id}`}
              onClick={this.handleLinkClick(`/${type}/${section.id}`)}
            >
              {section.name}
            </Link>
            <Typography color="textPrimary">Edit Topic</Typography>
          </Breadcrumbs>
        </div>

        <div className={classes.container}>
          <SimpleForm record={topic} onSubmit={this.onSubmit}>
            <NumberInput source="serialNo" validate={[required()]} {...inputOptions(3)} />
            <SelectInput source="subSectionId" validate={[required()]} choices={subSectionList} {...inputOptions(3)} />
            <TextInput source="name" validate={[required()]} {...inputOptions(3)} />
            <BooleanInput source="featured" xs={12} sm={3} />
            <RichTextInput
              label="Body"
              source="topicBody.content"
              validate={[required()]}
              xs={12}
              fullWidth
              options={{ modules, formats, minHeight: 6 }}
            />
          </SimpleForm>
        </div>
      </div>
    );
  }
}

const StyledCreateTopic = withStyles(styles)(CreateTopic);

export default props => (
  <DocsController {...props}>{controllerProps => <StyledCreateTopic {...props} {...controllerProps} />}</DocsController>
);
