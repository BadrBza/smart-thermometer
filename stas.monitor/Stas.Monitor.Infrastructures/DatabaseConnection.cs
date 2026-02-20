namespace Stas.Monitor.Infrastructures;
using MySql.Data.MySqlClient;
using System.Data;

    public class DatabaseConnection : IDatabaseConnectionFactory
    {
        private readonly string _connectionString;

        public DatabaseConnection(string url, string user, string password)
        {
            var uri = new Uri(url.Replace("jdbc:mysql://", "http://"));
            string server = uri.Host;
            string port = uri.Port.ToString();
            string database = uri.AbsolutePath.TrimStart('/');

            _connectionString = new MySqlConnectionStringBuilder
            {
                Server = server,
                Port = uint.Parse(port),
                UserID = user,
                Password = password,
                Database = database,
            }.ConnectionString;
        }

        public IDbConnection CreateConnection()
        {
            return new MySqlConnection(_connectionString) as IDbConnection;
        }
    }

