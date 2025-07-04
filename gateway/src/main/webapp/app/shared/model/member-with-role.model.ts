export interface MemberWithRole {
  userLogin: string;
  userName: string;
  role: 'OWNER' | 'MODERADOR' | 'MIEMBRO';
  joinDate: string;
}
