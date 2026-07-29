import SwiftUI
import Shared

@main
struct iOSApp: App {
    init() {
        KoinInitIOSKt.doInitKoinIos()
    }
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
