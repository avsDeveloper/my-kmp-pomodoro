import SwiftUI
import shared

@main
struct iOSApp: App {
    init() {
        KoinIOSKt.doInitKoin()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

struct ContentView: View {
    private let viewModel: TimerViewModel

    init() {
        self.viewModel = IosKoinHelper.shared.getTimerViewModel()
    }

    var body: some View {
        ComposeView(viewModel: viewModel)
            .ignoresSafeArea(.all)
            .onDisappear {
                viewModel.onCleared()
            }
    }
}

struct ComposeView: UIViewControllerRepresentable {
    var viewModel: TimerViewModel

    func makeUIViewController(context: Context) -> UIViewController {
        return MainViewControllerKt.MainViewController(viewModel: viewModel)
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
