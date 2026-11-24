// Realm roles of the Keycloak realm (realm-bank.json).
export const ROLES = ['ADVISOR', 'COMPLIANCE_OFFICER', 'ADMIN'] as const;

export type Role = (typeof ROLES)[number];
