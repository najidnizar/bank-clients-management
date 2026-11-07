{{/* Labels recommended by Kubernetes, put on every object of the release */}}
{{- define "bank-service.labels" -}}
{{ include "bank-service.selectorLabels" . }}
app.kubernetes.io/part-of: bank-clients-management
app.kubernetes.io/managed-by: {{ .Release.Service }}
helm.sh/chart: {{ .Chart.Name }}-{{ .Chart.Version }}
{{- end }}

{{/* Labels used to select the pods of the release (must never change after the first install) */}}
{{- define "bank-service.selectorLabels" -}}
app.kubernetes.io/name: {{ .Release.Name }}
{{- end }}
