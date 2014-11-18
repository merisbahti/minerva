/**
 * Copyright 2011 The Open Source Research Group,
 *                University of Erlangen-Nürnberg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *//*

package jules;

import org.sweble.wikitext.engine.PageId;
import org.sweble.wikitext.engine.PageTitle;
import org.sweble.wikitext.engine.WtEngineImpl;
import org.sweble.wikitext.engine.config.WikiConfig;
import org.sweble.wikitext.engine.config.WikiConfigurationInterface;
import org.sweble.wikitext.engine.nodes.EngProcessedPage;
import org.sweble.wikitext.engine.utils.DefaultConfigEnWp;

import edu.jhu.nlp.wikipedia.WikiPage;

public class App
{
	public static void main(String[] args){
		try {
			System.out.println(run(null));
		} catch (Exception e) {e.printStackTrace();}
	}
	static String run(WikiPage page) throws Exception
	{
		WikiPage p = new WikiPage();
		p.setWikiText(new String("== Simple Page == \n"
				+ "\n"
				+ "Hi! This is a '''simple page'''. If everything works as expected this page will be\n"
				+ "* encoding validated\n"
				+ "* preprocessed\n"
				+ "* parsed\n"
				+ "* postprocessed\n"
				+ "* and finally rendered as HTML.\n"
				+ "\n"
				+ "However, template expansion will not be performed.\n"
				+ "\n"
				+ "Good Luck!"));
		// Set-up a simple wiki configuration
		WikiConfig config = DefaultConfigEnWp.generate();
		
		final int wrapCol = 80;
		
		// Instantiate a compiler for wiki pages
		WtEngineImpl engine = new WtEngineImpl(config);
		PageId pageId = new PageId(PageTitle.make((WikiConfigurationInterface) config, p.getTitle()), -1);
		
		String wikitext = p.getWikiText();
		
		// Compile the retrieved page
		EngProcessedPage cp = engine.postprocess(pageId, wikitext, null);
		
		TextConverter tv = new TextConverter(config, wrapCol);
		return (String) tv.go(cp.getPage());
	}
}
*/