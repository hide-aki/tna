import { Role } from "./types";

export default (roles = [], hasAccess = () => {}, resourceId, scope, action) => {
  return (
    roles.includes(Role.MASTER) ||
    roles.includes(Role.SUPER_USER) ||
    roles.includes(Role.ADMIN_USER) ||
    hasAccess(resourceId, scope, action)
  );
};
