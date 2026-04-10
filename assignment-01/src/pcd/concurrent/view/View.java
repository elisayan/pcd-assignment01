package pcd.concurrent.view;


import pcd.concurrent.view.ViewFrame;
import pcd.concurrent.view.ViewModel;

public class View {

	private ViewFrame frame;
	private ViewModel viewModel;
	
	public View(ViewModel model, int w, int h) {
		frame = new ViewFrame(model, w, h);	
		frame.setVisible(true);
		this.viewModel = model;
	}
		
	public void render() {
		frame.render();
	}
	
	public ViewModel getViewModel() {
		return viewModel;
	}
}
