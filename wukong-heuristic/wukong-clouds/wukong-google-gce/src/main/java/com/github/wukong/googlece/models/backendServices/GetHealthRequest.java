package com.github.wukong.googlece.models.backendServices;

public class GetHealthRequest implements com.github.wukong.googlece.models.AbstractGoogleRequest {
	protected java.lang.String project;

	protected java.lang.String backendService;

	protected com.google.api.services.compute.model.ResourceGroupReference content;

	public void setProject(java.lang.String project) {
		this.project = project;
}
	public java.lang.String getProject() {
		return this.project;
}
	public void setBackendService(java.lang.String backendService) {
		this.backendService = backendService;
}
	public java.lang.String getBackendService() {
		return this.backendService;
}
	public void setContent(com.google.api.services.compute.model.ResourceGroupReference content) {
		this.content = content;
}
	public com.google.api.services.compute.model.ResourceGroupReference getContent() {
		return this.content;
}
}