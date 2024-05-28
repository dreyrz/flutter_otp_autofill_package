import 'package:flutter/material.dart';

import 'package:otp_autofill_package/otp_autofill_package.dart';

import 'input.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  late final OtpAutofillPackage smsListener;
  late final List<TextEditingController> controllers;

  @override
  void initState() {
    smsListener = OtpAutofillPackage();
    controllers = List.generate(6, (_) => TextEditingController());
    super.initState();
  }

  void listen() {
    smsListener.startListening(onOtpReceived: fillInputs);
  }

  void destroy() {
    smsListener.dispose();
  }

  void fillInputs(String otp) {
    debugPrint("fillInputs");
    if (otp.length == 6) {
      for (int i = 0; i < controllers.length; i++) {
        controllers[i].text = otp[i];
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Expanded(
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    ElevatedButton(
                      onPressed: () => listen(),
                      child: const Text('SMS Retriever'),
                    ),
                    ElevatedButton(
                      onPressed: destroy,
                      child: const Text('Dispose listener'),
                    ),
                  ],
                ),
              ),
              Expanded(
                child: Row(
                  children: List.generate(6, (i) => Input(controllers[i])),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
