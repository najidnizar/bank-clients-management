// ng build: Keycloak exposed by the Kubernetes Gateway API.
export const environment = {
  keycloak: {
    url: 'http://keycloak.localhost',
    realm: 'bank',
    clientId: 'bank-frontend',
  },
};
