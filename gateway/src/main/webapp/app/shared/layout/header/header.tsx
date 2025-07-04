import './header.scss';

import React, { useState } from 'react';

import { Collapse, Nav, Navbar, NavbarToggler, NavItem, NavLink } from 'reactstrap';
import LoadingBar from 'react-redux-loading-bar';

import { AccountMenu, AdminMenu, EntitiesMenu } from '../menus';
import { Brand, Home } from './header-components';

export interface IHeaderProps {
  isAuthenticated: boolean;
  isAdmin: boolean;
  ribbonEnv: string;
  isInProduction: boolean;
  isOpenAPIEnabled: boolean;
  account?: any; // Añadimos la información de la cuenta
}

const Header = (props: IHeaderProps) => {
  const [menuOpen, setMenuOpen] = useState(false);

  const renderDevRibbon = () =>
    props.isInProduction === false ? (
      <div className="ribbon dev">
        <a href="">Development</a>
      </div>
    ) : null;

  const toggleMenu = () => setMenuOpen(!menuOpen);

  const renderUserInfo = () => {
    if (props.isAuthenticated && props.account) {
      const authorities = props.account.authorities || [];
      const roles = authorities.join(', ');

      return (
        <NavItem className="user-info d-flex align-items-center">
          <NavLink className="text-light">
            <span className="user-name">
              <i className="fas fa-user me-1"></i>
              {props.account.login}
            </span>
            <span className="badge bg-secondary user-roles">
              <i className="fas fa-shield-alt me-1"></i>
              {roles || 'Usuario'}
            </span>
          </NavLink>
        </NavItem>
      );
    }
    return null;
  };

  /* jhipster-needle-add-element-to-menu - JHipster will add new menu items here */

  return (
    <div id="app-header">
      {renderDevRibbon()}
      <LoadingBar className="loading-bar" />
      <Navbar data-cy="navbar" dark expand="md" fixed="top" className="bg-primary">
        <NavbarToggler aria-label="Menu" onClick={toggleMenu} aria-expanded={menuOpen} />
        <Brand />
        <Collapse isOpen={menuOpen} navbar>
          <Nav id="header-tabs" className="ms-auto" navbar role="navigation" aria-label="Navegación principal">
            <Home />
            {props.isAuthenticated && <EntitiesMenu />}
            {props.isAuthenticated && props.isAdmin && <AdminMenu showOpenAPI={props.isOpenAPIEnabled} />}
            {renderUserInfo()}
            <AccountMenu isAuthenticated={props.isAuthenticated} />
          </Nav>
        </Collapse>
      </Navbar>
    </div>
  );
};

export default Header;
