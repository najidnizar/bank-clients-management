/** Application roles, defined as realm roles in Keycloak (realm-bank.json). */
export const ROLES = ['ADVISOR', 'COMPLIANCE_OFFICER', 'ADMIN'] as const;

/** 'ADVISOR' | 'COMPLIANCE_OFFICER' | 'ADMIN', derived from the array above. */
export type Role = (typeof ROLES)[number];
