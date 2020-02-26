import React, { Component } from "react";

import { withStyles } from "@material-ui/core/styles";

import Breadcrumbs from "@material-ui/core/Breadcrumbs";
import Typography from "@material-ui/core/Typography";
import Link from "@material-ui/core/Link";

import NavigateNextIcon from "@material-ui/icons/NavigateNext";
import CreateIcon from "@material-ui/icons/Edit";

import { Button } from "jazasoft";

import DocsController from "./DocsController";
import DocPage from "../../components/DocPage";
import { Role } from "../../utils/types";

const styles = theme => ({
  root: {
    flexGrow: 1,
    display: "flex"
  },
  header: {
    margin: "1em 1.5em",
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center"
  },
  container: {
    flexGrow: 1
  },
  content: {
    "& h1, & h2, & h3, & h4, & h5, & h6": {
      color: "#000"
    },
    "& h1": {
      fontSize: "2em",
      fontWeight: 400
    },
    "& h2": {
      fontSize: "1.6em",
      fontWeight: 400
    },
    "& strong": {
      fontWeight: 600
    },
    "& a": {
      color: "blue"
    },
    "& p": {
      [theme.breakpoints.down("md")]: {
        fontSize: "0.9rem"
      },
      [theme.breakpoints.up("md")]: {
        fontSize: "0.9rem"
      },
      lineHeight: "1.5em",
      color: "#000",
      marginBottom: "10px"
    },
    "& li": {
      [theme.breakpoints.down("md")]: {
        fontSize: "0.9rem"
      },
      [theme.breakpoints.up("md")]: {
        fontSize: "0.9rem"
      },
      lineHeight: "1.5em",
      color: "#000"
    },
    "& img": {
      maxWidth: "100%",
      height: "auto",
      borderRadius: "10px",
      boxShadow: "5px -5px 15px -8px rgba(0, 0, 0, 0.24), -10px 10px 10px -5px rgba(0, 0, 0, 0.2)"
    },
    "& blockquote": {
      padding: "10px 20px",
      margin: "0 0 20px",
      fontSize: "1.25rem",
      borderLeft: "5px solid #eee"
    }
  }
});

class Topic extends Component {
  componentDidMount() {
    const section = (this.props.location.state && this.props.location.state.section) || {};
    this.props.fetchTopics(`subSection.section.id==${section.id}`);
  }

  onSubmit = values => {
    this.props.saveOrUpdateTopic("save", values);
  };

  handleLinkClick = path => e => {
    e.preventDefault();
    this.props.history.push(path);
  };

  render() {
    const { classes, roles, subSections = {}, topics = {}, history, location, match } = this.props;
    const type = location.pathname.includes("/help") ? "help" : "manual";

    const section = (location.state && location.state.section) || {};
    const subSectionList = Object.keys(subSections)
      .map(id => subSections[id])
      .filter(e => e.section.id === section.id);
    const topic = topics[match.params.id] || {};
    const topicList = Object.keys(topics).map(id => topics[id]);

    let navLinks =
      subSectionList.length === 1
        ? topicList
            .filter(e => e.subSection && e.subSection.id === subSectionList[0].id)
            .sort((a, b) => a.serialNo - b.serialNo)
            .map(({ name, id }) => ({ name, path: `/${type}/topics/${id}/view`, state: { section } }))
        : subSectionList.map(({ name, id }) => ({
            title: name,
            navLinks: topicList
              .filter(e => e.subSection.id === id)
              .sort((a, b) => a.serialNo - b.serialNo)
              .map(({ name, id }) => ({ name, path: `/${type}/topics/${id}/view`, state: { section } }))
          }));
    const headerElement = (
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
          <Typography color="textPrimary">{topic.name}</Typography>
        </Breadcrumbs>
        {roles && roles.includes(Role.MASTER) && (
          <Button
            label="Edit"
            color="secondary"
            variant="contained"
            onClick={_ => history.push(`/${type}/topics/${topic.id}/edit`, { section })}
          >
            <CreateIcon />
          </Button>
        )}
      </div>
    );
    return (
      <div className={classes.root}>
        <div className={classes.container}>
          <DocPage headerElement={headerElement} navTitle={section.name} navLinks={navLinks} contentWidth={10}>
            <div>
              <Typography variant="h5">{topic && topic.name}</Typography>
            </div>
            <div className={classes.content}>
              <span dangerouslySetInnerHTML={{ __html: topic && topic.topicBody && topic.topicBody.content }} />
            </div>
          </DocPage>
        </div>
      </div>
    );
  }
}

const StyledTopic = withStyles(styles)(Topic);

export default props => (
  <DocsController {...props}>{controllerProps => <StyledTopic {...props} {...controllerProps} />}</DocsController>
);
