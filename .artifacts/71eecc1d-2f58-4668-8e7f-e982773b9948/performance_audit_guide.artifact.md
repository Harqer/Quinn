# Mave Studio: High-Fidelity Performance Audit Guide

To analyze the performance of the Lyria engine and Studio HUD with the **Perfetto Trace Analysis** skill, follow these steps to capture a high-fidelity trace.

## 1. Capture a Perfetto Trace via ADB

Run this command while performing a critical action in the app (e.g., clicking "Strike a Vibe"):

```bash
adb shell perfetto \
  -c - --txt \
  -o /data/misc/perfetto-traces/mave_trace.perfetto-trace <<EOF
buffers: {
    size_kb: 63488
    fill_policy: DISCARD
}
data_sources: {
    config {
        name: "linux.process_stats"
        target_buffer: 0
        scan_all_processes_on_start: true
    }
}
data_sources: {
    config {
        name: "android.surfaceflinger.frametimeline"
    }
}
data_sources: {
    config {
        name: "android.sdk_sysprop_guard"
    }
}
data_sources: {
    config {
        name: "linux.sys_stats"
        sys_stats_config {
            stat_period_ms: 1000
            stat_counters: STAT_CPU_TOTAL
            stat_counters: STAT_VMSTAT_PGFAULT
        }
    }
}
data_sources: {
    config {
        name: "android.ftrace"
        ftrace_config {
            ftrace_events: "sched/sched_switch"
            ftrace_events: "power/cpu_frequency"
            ftrace_events: "power/cpu_idle"
            ftrace_events: "sched/sched_process_exit"
            ftrace_events: "sched/sched_process_free"
            ftrace_events: "task/task_newtask"
            ftrace_events: "task/task_rename"
            atrace_categories: "am"
            atrace_categories: "wm"
            atrace_categories: "dalvik"
            atrace_categories: "view"
            atrace_categories: "sched"
            atrace_categories: "gfx"
            atrace_apps: "com.musically.studio"
        }
    }
}
duration_ms: 10000
EOF
```

## 2. Pull the Trace to your Machine

```bash
adb pull /data/misc/perfetto-traces/mave_trace.perfetto-trace .
```

## 3. Provide the Trace to the Agent

Once you have the `mave_trace.perfetto-trace` file, let me know its path, and I will begin the **Depth-First Root Cause Analysis**.

---

### What I will look for:
- **Jank in POV Stream**: Are camera frames causing Main Thread stalls?
- **Lyria Latency**: Is the gap between "Vibe Intent" and "Audio Output" caused by network, scheduling, or rendering?
- **Thermal Pressure**: Is the continuous AI reasoning causing CPU throttling on the wearable?
