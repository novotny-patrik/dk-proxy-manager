import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './proxy.reducer';

export const ProxyDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const proxyEntity = useAppSelector(state => state.proxy.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="proxyDetailsHeading">
          <Translate contentKey="dkProxyManagerApp.proxy.detail.title">Proxy</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{proxyEntity.id}</dd>
          <dt>
            <span id="ipAddress">
              <Translate contentKey="dkProxyManagerApp.proxy.ipAddress">Ip Address</Translate>
            </span>
          </dt>
          <dd>{proxyEntity.ipAddress}</dd>
          <dt>
            <span id="port">
              <Translate contentKey="dkProxyManagerApp.proxy.port">Port</Translate>
            </span>
          </dt>
          <dd>{proxyEntity.port}</dd>
          <dt>
            <span id="username">
              <Translate contentKey="dkProxyManagerApp.proxy.username">Username</Translate>
            </span>
          </dt>
          <dd>{proxyEntity.username}</dd>
          <dt>
            <span id="password">
              <Translate contentKey="dkProxyManagerApp.proxy.password">Password</Translate>
            </span>
          </dt>
          <dd>{proxyEntity.password}</dd>
          <dt>
            <span id="active">
              <Translate contentKey="dkProxyManagerApp.proxy.active">Active</Translate>
            </span>
          </dt>
          <dd>{proxyEntity.active ? 'true' : 'false'}</dd>
        </dl>
        <Button tag={Link} to="/proxy" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/proxy/${proxyEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ProxyDetail;
