import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './tw-account.reducer';

export const TwAccountDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const twAccountEntity = useAppSelector(state => state.twAccount.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="twAccountDetailsHeading">
          <Translate contentKey="dkProxyManagerApp.twAccount.detail.title">TwAccount</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{twAccountEntity.id}</dd>
          <dt>
            <span id="username">
              <Translate contentKey="dkProxyManagerApp.twAccount.username">Username</Translate>
            </span>
          </dt>
          <dd>{twAccountEntity.username}</dd>
          <dt>
            <span id="password">
              <Translate contentKey="dkProxyManagerApp.twAccount.password">Password</Translate>
            </span>
          </dt>
          <dd>{twAccountEntity.password}</dd>
          <dt>
            <span id="active">
              <Translate contentKey="dkProxyManagerApp.twAccount.active">Active</Translate>
            </span>
          </dt>
          <dd>{twAccountEntity.active ? 'true' : 'false'}</dd>
          <dt>
            <Translate contentKey="dkProxyManagerApp.twAccount.proxy">Proxy</Translate>
          </dt>
          <dd>{twAccountEntity.proxy ? twAccountEntity.proxy.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/tw-account" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/tw-account/${twAccountEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default TwAccountDetail;
