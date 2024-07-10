import { IProxy } from 'app/shared/model/proxy.model';

export interface ITwAccount {
  id?: number;
  username?: string;
  password?: string;
  active?: boolean | null;
  proxy?: IProxy | null;
}

export const defaultValue: Readonly<ITwAccount> = {
  active: false,
};
