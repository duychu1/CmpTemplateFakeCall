import org.koin.android.ext.koin.androidContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            startKoin {
                androidContext(this@MainActivity)
                modules(appModule, platformModule)
            }
            App()
        }
    }
} 