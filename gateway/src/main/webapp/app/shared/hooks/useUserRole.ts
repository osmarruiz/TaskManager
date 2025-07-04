import { useAppSelector } from 'app/config/store';
import { hasAnyAuthority } from 'app/shared/auth/private-route';
import { AUTHORITIES } from 'app/config/constants';

export const useUserRole = () => {
  const account = useAppSelector(state => state.authentication.account);
  const isAdmin = hasAnyAuthority(account.authorities, [AUTHORITIES.ADMIN]);
  const isAuthenticated = useAppSelector(state => state.authentication.isAuthenticated);

  return {
    isAdmin,
    account,
    isAuthenticated,
    authorities: account.authorities || [],
  };
};
