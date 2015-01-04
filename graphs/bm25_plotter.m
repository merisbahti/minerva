function bm25_plotter()
  graphics_toolkit ("gnuplot")
  % Scan the data from the incomplete data-set.
  [xbm25,ybm25] = scan_data("data/v1_bm25_tfdf/bm25.txt");
  [xtfdf,ytfdf] = scan_data("data/v1_bm25_tfdf/tfdf.txt");
  [xbm25doc,ybm25doc] = scan_data("data/v1_bm25_tfdf/bm25-doc.txt");
  [xtfdfdoc,ytfdfdoc] = scan_data("data/v1_bm25_tfdf/tfdf-doc.txt");
  plot(xbm25doc,ybm25doc,'marker','o','color', [46/255 204/255 113/255],'LineWidth',4);
  hold on;
  plot(xtfdfdoc,ytfdfdoc,'marker','o','color', [155/255 89/255 182/255],'LineWidth',4);
  plot(xbm25,ybm25,'marker','o','color', [230/255 126/255 34/255],'LineWidth',4);
  plot(xtfdf,ytfdf,'marker','o','color', [52/255 152/255 219/255],'LineWidth',4);
  xlim([1 350])


  h = legend('BM25 Documents', 'TF-IDF Documents', 'BM25 Paragraphs', 'TF-IDF Paragraphs', 'location', 'southeast');
  set(h,'FontSize',18); 
  xlabel('#Passages')
  ylabel('Answers present')
  a=[cellstr(num2str(get(gca,'ytick')'*100))]; 
  pct = char(ones(size(a,1),1)*'%'); 
  new_yticks = [char(a),pct];
  set(gca,'yticklabel',new_yticks) 
  set(gca,'FontSize',7,'LineWidth',2)
  set(gcf, 'PaperPosition', [0 0 8 7]); %Position plot at left hand corner with width 5 and height 5.
  set(gcf, 'PaperSize', [8 7]); %Set the paper to have width 5 and height 5.
  set([gca; findall(gca, 'Type','text')], 'FontSize', 18);
  saveas(gcf, 'bm25_tfdf', 'pdf') %Save figure
  %print('-dpng','-r96','test')
  axis equal;
  hold off;
