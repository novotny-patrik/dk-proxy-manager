export interface IProxy {
  id?: number;
  ipAddress?: string;
  port?: number;
  username?: string | null;
  password?: string | null;
  active?: boolean | null;
}

export const defaultValue: Readonly<IProxy> = {
  active: false,
};
