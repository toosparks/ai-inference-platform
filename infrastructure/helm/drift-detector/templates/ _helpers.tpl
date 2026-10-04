{{/*
Имя чарта (не длиннее 63 символов)
*/}}
{{- define "drift-detector.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Полное имя релиза + чарт
*/}}
{{- define "drift-detector.fullname" -}}
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
{{- define "drift-detector.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Общие лейблы
*/}}
{{- define "drift-detector.labels" -}}
helm.sh/chart: {{ include "drift-detector.chart" . }}
{{ include "drift-detector.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Селектор-лейблы (используются в Service и Deployment)
*/}}
{{- define "drift-detector.selectorLabels" -}}
app.kubernetes.io/name: {{ include "drift-detector.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{/*
Имя ServiceAccount
*/}}
{{- define "drift-detector.serviceAccountName" -}}
{{- if .Values.serviceAccount.create }}
{{- default (include "drift-detector.fullname" .) .Values.serviceAccount.name }}
{{- else }}
{{- default "default" .Values.serviceAccount.name }}
{{- end }}
{{- end }}