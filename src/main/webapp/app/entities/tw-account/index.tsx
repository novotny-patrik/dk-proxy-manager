import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import TwAccount from './tw-account';
import TwAccountDetail from './tw-account-detail';
import TwAccountUpdate from './tw-account-update';
import TwAccountDeleteDialog from './tw-account-delete-dialog';

const TwAccountRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<TwAccount />} />
    <Route path="new" element={<TwAccountUpdate />} />
    <Route path=":id">
      <Route index element={<TwAccountDetail />} />
      <Route path="edit" element={<TwAccountUpdate />} />
      <Route path="delete" element={<TwAccountDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default TwAccountRoutes;
