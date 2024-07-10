import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Proxy from './proxy';
import ProxyDetail from './proxy-detail';
import ProxyUpdate from './proxy-update';
import ProxyDeleteDialog from './proxy-delete-dialog';

const ProxyRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Proxy />} />
    <Route path="new" element={<ProxyUpdate />} />
    <Route path=":id">
      <Route index element={<ProxyDetail />} />
      <Route path="edit" element={<ProxyUpdate />} />
      <Route path="delete" element={<ProxyDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ProxyRoutes;
