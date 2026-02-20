using Stas.Monitor.Domains;

namespace Stas.Monitor.Infrastructures
{
    public class IniConfigurationReader : IConfigurationReader
    {
        private const int NbPartiesMinRequis = 2;

        private readonly IDictionary<string, Dictionary<string, string>> _sections = new Dictionary<string, Dictionary<string, string>>();
        private int _thermometerCount;

        public IniConfigurationReader(Stream configStream)
        {
            if (configStream == null)
            {
                throw new FileNotFoundException("Configuration stream cannot be null.");
            }

            using (var reader = new StreamReader(configStream))
            {
                LoadFromConfig(reader);
            }
        }

        public void LoadFromConfig(TextReader reader)
        {
            string currentSection = string.Empty;
            string line;
            while ((line = reader.ReadLine()) != null)
            {
                ProcessConfigLine(ref currentSection, line.Trim());
            }
        }

        private void ProcessConfigLine(ref string currentSection, string line)
        {
            if (IsSection(line))
            {
                currentSection = ParseSection(line);
            }
            else if (IsKeyPair(currentSection, line))
            {
                AddKeyValuePairToSection(currentSection, line);
            }
        }

        private bool IsSection(string line) => line.StartsWith("[") && line.EndsWith("]");

        private bool IsKeyPair(string currentSection, string line) => !string.IsNullOrEmpty(currentSection) && line.Contains("=");

        private string ParseSection(string line)
        {
            string sectionName = line.Substring(1, line.Length - 2).Trim();
            string sectionKey = sectionName.Equals("general", StringComparison.OrdinalIgnoreCase)
                ? sectionName + "_" + (++_thermometerCount)
                : sectionName + "_" + _thermometerCount;

            if (!_sections.ContainsKey(sectionKey))
            {
                _sections[sectionKey] = new Dictionary<string, string>();
            }

            return sectionKey;
        }

        private void AddKeyValuePairToSection(string currentSection, string line)
        {
            var parts = line.Split(new[] { '=' }, 2);
            if (parts.Length == NbPartiesMinRequis)
            {
                _sections[currentSection][parts[0].Trim()] = parts[1].Trim();
            }
        }

        public string Get(string section, string key)
        {
            return _sections.TryGetValue(section, out var sectionData)
                   && sectionData.TryGetValue(key, out var value)
                ? value
                : string.Empty;
        }

        public Configuration GetThermometerConfig()
        {
            var config = BuildThermometerConfig();

            return new Configuration(config)
            {
                ConfigThermometer = config,
            };
        }

        private IList<Thermometer> BuildThermometerConfig()
        {
            var config = new List<Thermometer>();
            for (int i = 1; i <= _thermometerCount; i++)
            {
                config.Add(CreateThermometer(i));
            }

            return config;
        }

        private Thermometer CreateThermometer(int index)
        {
            var generalSection = $"general_{index}";
            var formatSection = $"format_{index}";
            var profileSection = $"profile_{index}";

            return new Thermometer(
                Name: Get(generalSection, "name"),
                DateTimeFormat: Get(formatSection, "datetime"),
                TemperatureFormat: Get(formatSection, "temperature"),
                Profile: _sections.TryGetValue(profileSection, out var sectionData)
                    ? sectionData
                        .Where(kv => kv.Key.StartsWith("j"))
                        .ToDictionary(kv => kv.Key, kv =>
                            int.TryParse(kv.Value, out var intValue)
                                ? intValue
                                : throw new FormatException($"Unable to parse profile value '{kv.Value}' as integer."))
                    : new Dictionary<string, int>());
        }

        public string GetCsvPath() => Get("csv_0", "path");

        public string GetCsvPathAlerte() => Get("csv_0", "pathAlerte");

        public string GetCsvPathHumidity() => Get("csv_0", "pathHumidite");

        public string GetCsvPathHumidityAlerts() => Get("csv_0", "pathHumiditeAlerte");
    }
}
