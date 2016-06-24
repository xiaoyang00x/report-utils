package com.intuit.tools.reporter.model;

/**
 * Object representing logged web page data. Internally used.
 */
public class WebLog extends AbstractLog {
	private String src;
	private String href;

	public WebLog() {
	}

	/**
	 * parse the string and build the object.
	 * 
	 * @param s
	 */
	public WebLog(String s) {
		super(s);
	}

	public String toString() {
		StringBuffer buff = new StringBuffer(super.toString());
		buff.append("||SRC=");
		if (src != null) {
			buff.append(src);
		}
		buff.append("||HREF=");
		if (href != null) {
			buff.append(href);
		}
		return buff.toString();
	}

	@Override
	protected void parse(String part) {
		if (part.startsWith("SRC=")) {
			src = part.replace("SRC=", "");
		} else if (part.startsWith("LOCATION=")) {
			setLocation(part.replace("LOCATION=", ""));
		} else if (part.startsWith("HREF=")) {
			href = part.replace("HREF=", "");
			if ("".equals(href)) {
				href = null;
			}
		} else if (getMsg() == null) {
			setMsg(part);
		}
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public String getHref() {
		return (href == null) ? "" : href.replaceAll("\\\\", "/");
	}

	public void setHref(String href) {
		this.href = href;
	}

	@Override
	public boolean hasLogs() {
		return (href != null || (getMsg() != null && !getMsg().trim().isEmpty()));
	}

}
