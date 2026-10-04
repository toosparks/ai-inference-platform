{{/*
Имя чарта (не длиннее 63 символов)
*/}}
{{- define "batch-processor.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Полное имя релиза + чарт
*/}}
{{- define "batch-processor.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- if contains $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}

{{/*
Имя чарта и версия как лейбл
*/}}
{{- define "batch-processor.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Общие лейблы
*/}}
{{- define "batch-processor.labels" -}}
helm.sh/chart: {{ include "batch-processor.chart" . }}
{{ include "batch-processor.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Селектор-лейблы (используются в Service и Deployment)
*/}}
{{- define "batch-processor.selectorLabels" -}}
app.kubernetes.io/name: {{ include "batch-processor.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{/*
Имя ServiceAccount
*/}}
{{- define "batch-processor.serviceAccountName" -}}
{{- if .Values.serviceAccount.create }}
{{- default (include "batch-processor.fullname" .) .Values.serviceAccount.name }}
{{- else }}
{{- default "default" .Values.serviceAccount.name }}
{{- end }}
{{- end }}