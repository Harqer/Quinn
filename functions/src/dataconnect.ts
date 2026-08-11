import { GoogleAuth } from 'google-auth-library';

const auth = new GoogleAuth({
  scopes: ['https://www.googleapis.com/auth/cloud-platform']
});

export async function executeMutation(operationName: string, variables: any) {
  let project = process.env.GCLOUD_PROJECT;
  if (!project) {
    project = await auth.getProjectId();
  }
  const client = await auth.getClient();
  const token = await client.getAccessToken();
  
  const url = `https://firebasedataconnect.googleapis.com/v1beta/projects/${project}/locations/us-central1/services/musically-studio:executeMutation`;

  const response = await fetch(url, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token.token}`
    },
    body: JSON.stringify({ operationName, variables })
  });
  
  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(`DataConnect error: ${errorText}`);
  }
  return response.json();
}
