/*
 * Remarkable API - Copyright (C) 2021 Matthias Wegner
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.rogatio.remarkable.api.model.content;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rogatio.remarkable.api.io.file.SvgTemplateLoader;
import org.rogatio.remarkable.api.model.web.ContentMetaData;

/**
 * The Class Notebook.
 * 
 * @author Matthias Wegner
 */
public class Content {

	/** The Constant logger. */
	protected static final Logger logger = LogManager.getLogger(Content.class);

	/** The id. */
	private String id;

	/** The name. */
	private String name;

	/** The type. */
	private String type;

	/** The current page. */
	private int currentPage;

	/** The template names. */
	private List<String> templateNames = new ArrayList<>();

	/** The meta data. */
	private ContentMetaData metaData;

	/** The content data. */
	private ContentData contentData;

	/**
	 * Instantiates a new content.
	 *
	 * @param metaData the meta data
	 */
	public Content(ContentMetaData metaData) {
		this.id = metaData.iD;
		this.name = metaData.vissibleName;
		setCurrentPageNumber(metaData.currentPage);
		this.metaData = metaData;
	}

	/**
	 * Instantiates a new notebook.
	 *
	 * @param id   the id
	 * @param name the name
	 */
	public Content(String id, String name) {
		this.id = id;
		this.name = name;
	}

	/**
	 * Gets the current page number.
	 *
	 * @return the current page number
	 */
	public int getCurrentPageNumber() {
		return currentPage;
	}

	/**
	 * Sets the current page number.
	 *
	 * @param currentPage the new current page number
	 */
	public void setCurrentPageNumber(int currentPage) {
		this.currentPage = currentPage;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/** The folders. */
	private List<String> folders = new ArrayList<String>();

	/** The pages. */
	private List<Page> pages = new ArrayList<Page>();

	/**
	 * Gets the folders.
	 *
	 * @return the folders
	 */
	public List<String> getFolders() {
		return folders;
	}

	/**
	 * Sets the folders.
	 *
	 * @param folders the new folders
	 */
	public void setFolders(List<String> folders) {
		this.folders = folders;
	}

	/**
	 * Gets the meta data.
	 *
	 * @return the meta data
	 */
	public ContentMetaData getMetaData() {
		return metaData;
	}

	/**
	 * Sets the meta data.
	 *
	 * @param metaData the new meta data
	 */
	public void setMetaData(ContentMetaData metaData) {
		this.metaData = metaData;
	}

	/**
	 * Gets the content data.
	 *
	 * @return the content data
	 */
	public ContentData getContentData() {
		return contentData;
	}

	/**
	 * Sets the content data.
	 *
	 * @param contentData the new content data
	 */
	public void setContentData(ContentData contentData) {
		this.contentData = contentData;
	}

	/**
	 * Gets the template file.
	 *
	 * @param page the page
	 * @return the template file
	 */
	public File getTemplateFile(Page page) {
		return SvgTemplateLoader.getInstance().getFile(getTemplateName(page));
	}

	/**
	 * Gets the default template.
	 *
	 * @return the default template
	 */
	public String getDefaultTemplate() {
		if (templateNames.size() > 0) {
			return templateNames.get(templateNames.size() - 1);
		}
		return null;
	}

	/**
	 * Gets the template name.
	 *
	 * @param page the page
	 * @return the template name
	 */
	public String getTemplateName(Page page) {
		String name = templateNames.get(page.getPageNumber());
		return name;
	}

	/**
	 * Gets the template names.
	 *
	 * @return the template names
	 */
	public List<String> getTemplateNames() {
		return templateNames;
	}

	/**
	 * Sets the template names.
	 *
	 * @param templateNames the new template names
	 */
	public void setTemplateNames(List<String> templateNames) {
		this.templateNames = templateNames;
	}

	/**
	 * Gets the page.
	 *
	 * @param number the number
	 * @return the page
	 */
	public Page getPage(int number) {
		for (Page remarkablePage : pages) {
			if (remarkablePage.getPageNumber() == number) {
				return remarkablePage;
			}
		}
		return null;
	}

	/**
	 * Adds the.
	 *
	 * @param page the page
	 */
	public void add(Page page) {
		page.setNotebook(this);
		pages.add(page);
	}

	/**
	 * Sets the pages.
	 *
	 * @param pages the new pages
	 */
	public void setPages(List<Page> pages) {
		this.pages = pages;

		for (Page page : pages) {
			page.setNotebook(this);
		}
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Gets the pages.
	 *
	 * @return the pages
	 */
	public List<Page> getPages() {
		return pages;
	}

	/**
	 * Gets the thumbnail.
	 *
	 * @return the thumbnail
	 */
	public File getThumbnail() {
		Page p = this.getPage(currentPage);
		if (p == null) {
			p = this.getPage(0);
			if (p == null) {
				return null;
			}
		}
//		String f = Util.getFilename(p, "_thumbnail", "png");
//		if (new File(f).exists()) {
//			return new File(f);
//		} else {
//			logger.info("Thumbnail image of " + new File(f).getAbsolutePath() + " not exists. Use export.");
//		}
		return p.getThumbnail();
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public Type getType() {
		return Type.get(type);
	}

	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Hash code.
	 *
	 * @return the int
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((pages == null) ? 0 : pages.hashCode());
		return result;
	}

	/**
	 * Equals.
	 *
	 * @param obj the obj
	 * @return true, if successful
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Content other = (Content) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (pages == null) {
			if (other.pages != null)
				return false;
		} else if (!pages.equals(other.pages))
			return false;
		return true;
	}
	
	

//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		Content other = (Content) obj;
//		if (id == null) {
//			if (other.id != null)
//				return false;
//		} else if (!id.equals(other.id))
//			return false;
//		return true;
//	}

}
