# 📈 NeuralQuant Terminal

A full-stack, autonomous algorithmic trading platform that bridges traditional quantitative finance with modern Generative AI. This platform streams live market data, executes mathematical momentum analysis, and leverages LLM-driven agentic workflows to inform trading decisions in real-time.

## 🚀 Overview
The NeuralQuant Terminal was designed to demonstrate high-frequency data ingestion and intelligent decision-making. By combining **Java's robust multithreading capabilities** with **Google Gemini's reasoning engine**, the system provides a seamless dashboard for monitoring algorithmic signals and paper-trading portfolio state.



## 🛠 Tech Stack
* **Backend:** Java 21, Spring Boot (WebSockets, REST, Scheduling, RestTemplate).
* **Frontend:** React (Vite), Recharts, Glassmorphism-based CSS.
* **Intelligence:** Google Gemini 2.5 Flash (Agentic reasoning for signal generation).
* **Data API:** Alpaca Markets (Live IEX data streaming).
* **Infrastructure:** Deployed via [Insert Cloud Provider, e.g., Railway/Vercel].

## 💡 Key Features
* **Real-time Data Pipeline:** Asynchronous WebSocket client with thread-safe `ConcurrentLinkedQueue` for zero-loss market data ingestion.
* **Quant Indicators Engine:** On-the-fly calculation of **RSI (14-period)** and **MACD (12/26 EMA)** using sliding-window algorithms.
* **AI Trading Agent:** A heuristic-driven LLM agent that ingests raw indicator snapshots and outputs structured JSON trade signals (`BUY`/`SELL`/`HOLD`).
* **Paper Portfolio OMS:** A thread-safe Order Management System to simulate capital allocation and track holdings.
* **Modern Terminal UI:** A dark-mode, "glassmorphism" dashboard featuring real-time price charting and automated decision visualization.

## 🏗 System Architecture
* **`client/`**: Handles low-latency WebSocket connections to market exchanges.
* **`calculator/`**: Deterministic math engines for technical analysis.
* **`service/`**: The orchestrator layer managing scheduled tasks and LLM API integrations.
* **`portfolio/`**: In-memory OMS tracking cash balances and asset positions.

## ⚡ Quick Start
1.  **Clone the repo:** `git clone [your-repo-url]`
2.  **Configure:** Add your `ALPACA_API_KEY` and `GEMINI_API_KEY` to `application.yml`.
3.  **Run Backend:** `mvn spring-boot:run`
4.  **Run Frontend:** `cd quant-dashboard && npm run dev`

---
*Built for 2028 Computer Engineering portfolio.*