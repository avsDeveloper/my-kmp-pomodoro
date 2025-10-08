import SwiftUI
import shared

@main
struct iOSApp: App {
    
    init() {
        // Initialize Koin
        KoinIOSKt.doInitKoin()
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}