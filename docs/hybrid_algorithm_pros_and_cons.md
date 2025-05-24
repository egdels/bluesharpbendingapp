# Vor- und Nachteile des HYBRID-Algorithmus

Der HYBRID-Algorithmus in der BlueSharpBendingApp kombiniert die Stärken von drei verschiedenen Tonerkennungsalgorithmen (YIN, MPM und FFT), um eine optimale Tonerkennung über den gesamten Frequenzbereich einer Mundharmonika zu gewährleisten.

## Funktionsweise

Der HYBRID-Algorithmus:
- Verwendet YIN für niedrige Frequenzen (unter 300 Hz)
- Verwendet MPM für mittlere Frequenzen (300-1000 Hz)
- Verwendet FFT für hohe Frequenzen (über 1000 Hz)
- Analysiert das Audiosignal mit dem Goertzel-Algorithmus, um die Energieverteilung in verschiedenen Frequenzbereichen zu bestimmen
- Wählt automatisch den am besten geeigneten Algorithmus basierend auf den Frequenzmerkmalen aus
- Nutzt parallele Verarbeitung, um die Leistung zu optimieren

## Vorteile

1. **Beste Gesamtgenauigkeit**: Durch die Kombination der drei Algorithmen bietet HYBRID die beste Genauigkeit über den gesamten Frequenzbereich einer Mundharmonika.

2. **Optimierte Leistung für jeden Frequenzbereich**:
   - Nutzt YIN für niedrige Frequenzen, wo dieser Algorithmus am genauesten ist
   - Nutzt MPM für mittlere Frequenzen, wo dieser Algorithmus die beste Balance zwischen Genauigkeit und Geschwindigkeit bietet
   - Nutzt FFT für hohe Frequenzen, wo dieser Algorithmus am effizientesten ist

3. **Intelligente Frequenzanalyse**: Analysiert das Audiosignal, um den optimalen Algorithmus für die aktuelle Frequenz auszuwählen.

4. **Gute Leistung durch parallele Verarbeitung**: Nutzt moderne Mehrkernprozessoren effizient, um die Leistung zu optimieren.

5. **Zuverlässige Erkennung in verschiedenen Spielsituationen**: Passt sich an unterschiedliche Spieltechniken und Mundharmonika-Typen an.

6. **Gute Widerstandsfähigkeit gegenüber Hintergrundgeräuschen**: Kombiniert die Stärken der einzelnen Algorithmen, um auch in lauten Umgebungen zuverlässig zu funktionieren.

7. **Konsistente Konfidenzwerte**: Liefert über den gesamten Frequenzbereich konsistent hohe Konfidenzwerte.

8. **Schnellere Reaktionszeit als YIN bei mittleren und hohen Frequenzen**: Nutzt die Geschwindigkeitsvorteile von MPM und FFT in ihren optimalen Frequenzbereichen.

9. **Genauere Erkennung als FFT bei niedrigen Frequenzen**: Nutzt die Genauigkeitsvorteile von YIN im niedrigen Frequenzbereich.

## Nachteile

1. **Komplexere Implementierung**: Die Kombination mehrerer Algorithmen führt zu einer komplexeren Codebasis, die schwieriger zu warten und zu erweitern ist.

2. **Höherer Ressourcenverbrauch**: Kann auf älteren oder leistungsschwachen Geräten mehr Systemressourcen benötigen als einzelne Algorithmen.

3. **Höherer Batterieverbrauch auf mobilen Geräten**: Die parallele Verarbeitung und die Nutzung mehrerer Algorithmen kann zu einem höheren Energieverbrauch führen.

4. **Längere Initialisierungszeit**: Benötigt etwas mehr Zeit beim Start, um alle Algorithmen zu initialisieren.

5. **Potenzielle Verzögerungen bei schnellen Tonwechseln**: Die Entscheidung, welcher Algorithmus verwendet werden soll, kann bei sehr schnellen Tonwechseln zu leichten Verzögerungen führen.

6. **Nicht in der Web-Version verfügbar**: Aufgrund der höheren Ressourcenanforderungen ist der HYBRID-Algorithmus nur in der Desktop- und Android-Version verfügbar, nicht in der Web-Version.

## Empfehlung

Der HYBRID-Algorithmus wird empfohlen für:
- Fortgeschrittene Spieler, die maximale Genauigkeit über den gesamten Frequenzbereich benötigen
- Nutzer mit modernen Geräten, die über ausreichend Rechenleistung verfügen
- Situationen, in denen sowohl niedrige als auch hohe Frequenzen präzise erkannt werden müssen

Anfänger sollten mit dem YIN-Algorithmus beginnen, da dieser einfacher zu verstehen und weniger empfindlich gegenüber Variationen in der Spieltechnik ist. Mit zunehmender Erfahrung kann dann auf MPM oder HYBRID umgestiegen werden, um von der höheren Genauigkeit und schnelleren Reaktionszeit zu profitieren.