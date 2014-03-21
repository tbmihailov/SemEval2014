using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading.Tasks;

namespace LexiconToGazetteer
{
    class Program
    {
        static void Main(string[] args)
        {
            if (args.Length != 4)
            {
                Console.WriteLine("LineByLineRegexReplace replaces regex expression line by line from a text file");
                Console.WriteLine("");
                Console.WriteLine("4 params expected:");
                Console.WriteLine("1.input file");
                Console.WriteLine("2.regex");
                Console.WriteLine("3.regex replace");
                Console.WriteLine("4.output file");
            }

            string inputFile = args[0];
            string regexPattern = args[1];
            string regexReplace = args[2];
            string outputFile = args[3];

            using (TextReader reader = new StreamReader(inputFile))
            using(TextWriter writer = new StreamWriter(outputFile))
            {
                string inputLine = string.Empty;
                while ((inputLine = reader.ReadLine()) != null)
                {
                    string outputLine = Regex.Replace(inputLine, regexPattern, regexReplace);
                    writer.WriteLine(outputLine);
                }
            }
        }
    }
}
