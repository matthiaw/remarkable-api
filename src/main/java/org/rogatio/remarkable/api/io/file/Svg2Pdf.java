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
package org.rogatio.remarkable.api.io.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.fop.svg.PDFTranscoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.rogatio.remarkable.api.io.PropertiesCache;
import org.rogatio.remarkable.api.model.content.Content;
import org.rogatio.remarkable.api.model.content.Page;

/**
 * The Class Svg2Pdf.
 */
public class Svg2Pdf {

	/** The Constant logger. */
	private static final Logger logger = LogManager.getLogger(Svg2Pdf.class);

	/** The Constant EXPORTFOLDER. */
	private static final String EXPORTFOLDER = PropertiesCache.getInstance().getValue(PropertiesCache.EXPORTFOLDER);

	/**
	 * Merge.
	 *
	 * @param notebook the notebook
	 */
	public static void merge(Content notebook) {

		String folders = "";
		if (notebook.getFolders().size() > 0) {
			for (String f : notebook.getFolders()) {
				folders += f + File.separatorChar;
			}
			File ff = new File(EXPORTFOLDER + File.separatorChar + folders);
			ff.mkdirs();
		}

		String name = EXPORTFOLDER + File.separatorChar + folders + notebook.getName() + "_HD.pdf";
		File pdffile = new File(name);
		PDFMergerUtility pdf = new PDFMergerUtility();
		pdf.setDestinationFileName(pdffile.getPath());
		for (Page page : notebook.getPages()) {
			String pdfFile = Util.getFilename(page, "_HD", "pdf");
			logger.debug("Merge PDF '" + pdfFile+"' to PDF '"+name+"'");
				try {
				pdf.addSource(pdfFile);
			} catch (FileNotFoundException e) {
			}
		}
		try {
			pdf.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
			logger.info("Merge '" + name + "'");
		} catch (IOException e) {
			logger.error("Error merging '" + name + "' for notebook '" + notebook.getName() + "'", e);
		}
	}

	/**
	 * Convert.
	 *
	 * @param page the page
	 */
	public static void convert(Page page) {
		String svgFile = Util.getFilename(page, "svg");
		String pdfFile = Util.getFilename(page, "_HD", "pdf");

		logger.debug("Convert SVG to PDF: " + pdfFile);

		try {
			PDFTranscoder transcoder = new PDFTranscoder();
			TranscoderInput transcoderInput = new TranscoderInput(new FileInputStream(new File(svgFile)));
			TranscoderOutput transcoderOutput = new TranscoderOutput(new FileOutputStream(new File(pdfFile)));

			// Resize to A4, see default-value of PDFTranscoder.KEY_PIXEL_UNIT_TO_MILLIMETER
			float dpi = 0.264583f;

			String orientation = page.getNotebook().getContentData().getOrientation();

			if (orientation.equals("portrait")) {
				transcoder.addTranscodingHint(PDFTranscoder.KEY_WIDTH, page.getHorizontalWidth() / dpi);
				transcoder.addTranscodingHint(PDFTranscoder.KEY_HEIGHT, page.getVerticalWidth() / dpi);
			} else {
				transcoder.addTranscodingHint(PDFTranscoder.KEY_WIDTH, page.getVerticalWidth() / dpi);
				transcoder.addTranscodingHint(PDFTranscoder.KEY_HEIGHT, page.getHorizontalWidth() / dpi);
			}

			transcoder.transcode(transcoderInput, transcoderOutput);
		} catch (FileNotFoundException e) {
			logger.error("Error converting svg to pdf", e);
		} catch (TranscoderException e) {
			logger.error("Error converting svg to pdf", e);
		}
	}

}
