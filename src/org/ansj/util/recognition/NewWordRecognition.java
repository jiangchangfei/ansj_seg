package org.ansj.util.recognition;

import love.cq.domain.SmartForest;

import org.ansj.domain.Nature;
import org.ansj.domain.NewWord;
import org.ansj.domain.Term;
import org.ansj.domain.TermNatures;
import org.ansj.util.Graph;
import org.ansj.util.TermUtil;
import org.ansj.util.newWordFind.LearnTool;

/**
 * 新词识别
 * 
 * @author ansj
 * 
 */
public class NewWordRecognition {

	private Term[] terms = null;

	private double score;

	private StringBuilder sb = new StringBuilder();

	private SmartForest<NewWord> forest = null;

	private SmartForest<NewWord> branch = null;

	// private int offe = -1;
	// private int endOffe = -1;
	private TermNatures tempNatures;

	private Term from;

	private Term to;

	// 偏移量
	private int offe;

	public NewWordRecognition(Term[] terms, LearnTool learn) {
		this.terms = terms;
		forest = learn.getForest();
		branch = learn.getForest();
	}

	public void recognition() {
		if (branch == null) {
			return;
		}
		int length = terms.length - 1;

		Term term = null;
		for (int i = 0; i < length; i++) {
			if (terms[i] == null) {
				continue;
			} else {
				from = terms[i].getFrom();
				terms[i].score = 0;
				terms[i].selfScore = 0;
			}

			branch = branch.getBranch(terms[i].getName());

			if (branch == null || branch.getStatus() == 3) {
				reset();
				continue;
			}

			offe = i;

			// 循环查找添加
			term = terms[i];
			sb.append(term.getName());
			boolean flag = true;
			while (flag) {
				term = term.getTo();
				branch = branch.getBranch(term.getName());
				// 如果没有找到跳出
				if (branch == null) {
					break;
				}

				switch (branch.getStatus()) {
				case 1:
					sb.append(term.getName());
					continue;
				case 2:
					sb.append(term.getName());
					score = branch.getParam().getScore();
					tempNatures = branch.getParam().getNature();
					to = term.getTo();
					makeNewTerm();
					break;
				case 3:
					sb.append(term.getName());
					score = branch.getParam().getScore();
					tempNatures = branch.getParam().getNature();
					to = term.getTo();
					makeNewTerm();
					flag = false;
					break;
				default:
					System.out.println("怎么能出现0呢?");
					break;
				}
			}
			reset();
		}
	}

	private void makeNewTerm() {
		// TODO Auto-generated method stub
		Term term = new Term(sb.toString(), offe, tempNatures);
		term.score = score;
		TermUtil.termLink(from, term);
		TermUtil.termLink(term, to);
		TermUtil.insertTerm(terms, term);
	}

	/**
	 * 重置
	 */
	private void reset() {
		offe = -1;
		tempNatures = null;
		branch = forest;
		score = 0;
		sb = new StringBuilder();
	}

}
